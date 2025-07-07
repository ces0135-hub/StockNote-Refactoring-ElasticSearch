# Refactoring 내용

> MySQL 기반 검색을 ElasticSearch로 리팩토링하여 **98.8% 성능 개선**을 달성한 프로젝트

[![Java](https://img.shields.io/badge/Java-21-red.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green.svg)](https://spring.io/projects/spring-boot)
[![ElasticSearch](https://img.shields.io/badge/ElasticSearch-8.12.0-yellow.svg)](https://www.elastic.co/)
[![Performance](https://img.shields.io/badge/Performance-98.8%25%20Improved-brightgreen.svg)](#performance-results)

## [성능 개선 결과]

### [검색 성능 비교]
| 지표 | MySQL | ElasticSearch | 개선율 |
|------|-------|---------------|--------|
| **평균 응답시간** | 244.82ms | 2.83ms | **98.8% ↓** |
| **처리량** | 408 req/s | 35,335 req/s | **8,550% ↑** |
| **동시 사용자 500명** | 2,717ms | 6.65ms | **99.7% ↓** |

### [실제 테스트 결과]
```bash
# MySQL vs ElasticSearch 검색 성능 비교
curl "localhost:8080/api/v1/performance/compare-search?keyword=삼성전자&testCount=500"

{
  "keyword": "삼성전자",
  "testCount": 500,
  "mysqlResult": {
    "searchEngine": "MySQL",
    "averageResponseTime": 254.78,
    "throughput": 1962.48,
    "totalResults": 6710
  },
  "elasticSearchResult": {
    "searchEngine": "ElasticSearch", 
    "averageResponseTime": 3.04,
    "throughput": 164365.55,
    "totalResults": 6690
  },
  "performanceGain": {
    "responseTimeGainPercent": 98.81,
    "throughputGainPercent": 8275.4,
    "performanceLevel": "EXCELLENT"
  }
}
```

## [프로젝트 목표]

### 해결하고자 한 문제
1. **검색 성능 병목**: MySQL LIKE 쿼리로 인한 느린 검색 속도 (244ms)
2. **확장성 제약**: 검색 부하가 메인 DB에 집중되어 전체 시스템 성능 저하
3. **동시성 문제**: 다중 사용자 환경에서 응답시간 급증 (500명 → 2,717ms)
4. **기능 제한**: 복합 검색, 자동완성 등 고급 검색 기능 구현 어려움

### 달성한 목표
- **98.8% 응답시간 단축** (244ms → 2.83ms)
- **8,550% 처리량 증가** (408 → 35,335 req/s)
- **동시 사용자 500명 환경에서 안정적 서비스** (6.65ms 응답시간 유지)
- **확장 가능한 검색 아키텍처** 구축
- **체계적인 성능 테스트 인프라** 구축

## [시스템 아키텍처]

### Before: MySQL 기반 검색
```
Client Request
     ↓
Spring Boot Application
     ↓
MySQL Database (LIKE Query)
     ↓
⚠️ 성능 병목 발생 (244ms)
```

### After: ElasticSearch 기반 검색
```
Client Request
     ↓
Spring Boot Application
     ├─→ ElasticSearch (검색) → 2.83ms ⚡
     └─→ MySQL (데이터 저장)
```

### 데이터 동기화 전략
```java
@Transactional
public Post createPost(PostResponseDto postResponseDto, Member member) {
    // 1. MySQL에 데이터 저장
    Post savedPost = postRepository.save(post);
    
    // 2. ElasticSearch에 실시간 동기화
    PostDoc postDoc = searchDocService.transformPostDoc(post);
    keywordNotificationElasticService.createKeywordNotification(postDoc);
    
    return savedPost;
}
```

## [주요 기술 스택]

### 검색 엔진
- **ElasticSearch 8.12.0**: 고성능 전문 검색 엔진
- **Nori 분석기**: 한국어 형태소 분석
- **Multi-match 쿼리**: 다중 필드 검색 최적화

### 성능 테스트 & 모니터링
- **Custom Load Test Framework**: 동시성 테스트
- **System Resource Monitoring**: 실시간 CPU/메모리 모니터링
- **Performance Metrics**: 응답시간, 처리량, 에러율 측정

### 데이터 처리
- **Spring Data ElasticSearch**: 데이터 동기화
- **Redis**: 검색 결과 캐싱
- **CompletableFuture**: 비동기 처리
- **Batch Processing**: 대용량 데이터 효율적 처리

## [성능 테스트 구현]

### 1. 동시성 테스트
```java
@Service
public class LoadTestService {
    
    public LoadTestResult performConcurrentSearchTest(
            PostSearchConditionDto condition,
            int concurrentUsers,      // 동시 사용자 수
            int requestsPerUser,      // 사용자당 요청 수
            String searchEngine) {
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completeLatch = new CountDownLatch(concurrentUsers);
        
        AtomicLong successfulRequests = new AtomicLong(0);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();
        
        // 동시 사용자 시뮬레이션
        IntStream.range(0, concurrentUsers).forEach(userId -> {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // 모든 스레드가 동시에 시작
                    
                    for (int request = 0; request < requestsPerUser; request++) {
                        long startTime = System.nanoTime();
                        
                        // 검색 엔진별 요청 처리
                        if ("elasticsearch".equalsIgnoreCase(searchEngine)) {
                            searchDocService.searchPosts(condition, pageable);
                        } else {
                            postRepository.findByTitleContainingOrBodyContaining(
                                condition.getKeyword(), condition.getKeyword(), pageable);
                        }
                        
                        long responseTime = (System.nanoTime() - startTime) / 1_000_000;
                        responseTimes.add(responseTime);
                        successfulRequests.incrementAndGet();
                    }
                } catch (Exception e) {
                    failedRequests.incrementAndGet();
                } finally {
                    completeLatch.countDown();
                }
            });
        });
        
        startLatch.countDown();  // 테스트 시작
        completeLatch.await();   // 완료 대기
        
        return calculateResults(responseTimes, successfulRequests);
    }
}
```

### 2. 성능 메트릭 수집
```java
// 실제 측정된 성능 지표
ElasticSearch 성능 (동시 사용자 500명):
├─ 평균 응답시간: 6.65ms
├─ 95% 백분위수: 13ms  
├─ 99% 백분위수: 17ms
├─ 처리량: 454.13 req/s
├─ 에러율: 0%
└─ 성능 등급: EXCELLENT

MySQL 성능 (동시 사용자 100명):
├─ 평균 응답시간: 2,717ms
├─ 처리량: 17.55 req/s
├─ 에러율: 0%
└─ 성능 등급: POOR
```

## [API 엔드포인트]

### 성능 테스트 API
```bash
# 검색 성능 비교 테스트
GET /api/v1/performance/compare-search?keyword={키워드}&testCount={횟수}

# 부하 테스트 (MySQL)
POST /api/v1/performance/load-test?keyword=삼성전자&concurrentUsers=100&requestsPerUser=10&searchEngine=mysql

# 부하 테스트 (ElasticSearch)  
POST /api/v1/performance/load-test?keyword=삼성전자&concurrentUsers=500&requestsPerUser=20&searchEngine=elasticsearch

# 시스템 리소스 모니터링
GET /api/v1/performance/system-resources

# 시스템 통계 조회
GET /api/v1/performance/system-stats

# 대용량 더미 데이터 생성
POST /api/v1/performance/generate-dummy-data?postCount=100000&commentCount=500000
```

### 검색 API
```bash
# ElasticSearch 기반 게시글 검색
GET /api/v1/searchDocs/post/search?keyword={키워드}&searchType=ALL&category={카테고리}

# 종목 검색
POST /api/v1/searchDocs/stock
Content-Type: application/json
{
  "keyword": "삼성전자"
}

# 포트폴리오 통합 조회
GET /api/v1/searchDocs/myPortfolio
```

## [대용량 데이터 처리]

### 더미 데이터 생성 결과
- **121,100개 게시글** 생성
- **605,000개 댓글** 생성  
- **501명 회원** 데이터
- **데이터 규모**: LARGE (5만개 이상)

### 메모리 효율적 배치 처리
```java
@Transactional
public void generateLargeDummyData(int postCount, int commentCount) {
    log.info("🚀 대용량 더미 데이터 생성 시작 - 게시글: {}개, 댓글: {}개", postCount, commentCount);
    
    // 1. 테스트용 회원 생성 (100명)
    List<Member> testMembers = createTestMembers(100);
    
    // 2. 배치 단위로 게시글 생성 (메모리 효율성)
    int batchSize = 1000;
    List<Post> allPosts = new ArrayList<>();
    
    for (int batch = 0; batch < postCount / batchSize; batch++) {
        List<Post> batchPosts = new ArrayList<>();
        
        for (int i = 0; i < batchSize; i++) {
            Post post = createRealisticPost(testMembers, batch * batchSize + i);
            batchPosts.add(post);
        }
        
        // 배치 저장 후 즉시 메모리 해제
        List<Post> savedPosts = postRepository.saveAll(batchPosts);
        allPosts.addAll(savedPosts);
        batchPosts.clear();
        
        // ElasticSearch 비동기 동기화
        CompletableFuture.runAsync(() -> syncToElasticSearch(savedPosts));
        
        if ((batch + 1) % 10 == 0) {
            log.info("📄 게시글 {}개 생성 완료", (batch + 1) * batchSize);
        }
    }
}

private Post createRealisticPost(List<Member> members, int index) {
    String[] stockNames = {"삼성전자", "SK하이닉스", "LG에너지솔루션", "네이버", "카카오"};
    String[] keywords = {"매수", "매도", "분석", "전망", "추천"};
    
    Member randomMember = members.get(random.nextInt(members.size()));
    String stockName = stockNames[index % stockNames.length];
    String keyword = keywords[index % keywords.length];
    
    return Post.builder()
        .title(stockName + " " + keyword + " 관련 분석 " + (index + 1))
        .body(generateDetailedContent(stockName, keyword))
        .category(PostCategory.values()[index % PostCategory.values().length])
        .member(randomMember)
        .comments(new ArrayList<>())
        .likes(new ArrayList<>())
        .build();
}
```

## [ElasticSearch 최적화]

### 인덱스 설정 및 매핑
```json
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0,
    "analysis": {
      "analyzer": {
        "korean_analyzer": {
          "type": "nori",
          "decompound_mode": "mixed"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "korean_analyzer",
        "boost": 3.0
      },
      "body": {
        "type": "text", 
        "analyzer": "korean_analyzer"
      },
      "hashtags": {
        "type": "text",
        "boost": 2.0
      },
      "member_doc": {
        "type": "nested",
        "properties": {
          "name": {
            "type": "text",
            "boost": 2.0
          }
        }
      }
    }
  }
}
```

### 쿼리 최적화 전략
```java
// 1. Multi-match 쿼리로 필드별 가중치 적용
@Query("""
{
  "multi_match": {
    "query": "?0",
    "fields": ["title^3", "body", "member_doc.name^2", "hashtags^2"],
    "type": "best_fields"
  }
}
""")
Page<PostDoc> searchByAll(String keyword, Pageable pageable);

// 2. 카테고리별 필터링 쿼리
@Query("""
{
  "bool": {
    "must": [
      {"multi_match": {"query": "?0", "fields": ["title^3", "body", "hashtags^2"]}},
      {"term": {"category.keyword": "?1"}}
    ]
  }
}
""")
Page<PostDoc> searchByAllAndCategory(String keyword, String category, Pageable pageable);

// 3. 해시태그 기반 검색
@Query("""
{
  "match": {
    "hashtags": {
      "query": "?0"
    }
  }
}
""")
Page<PostDoc> searchByHashtag(String keyword, Pageable pageable);
```

## [모니터링 & 분석]

### 실시간 시스템 모니터링
```java
@Service
public class SystemMonitoringService {
    
    public SystemResourceInfo getCurrentSystemInfo() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            Runtime runtime = Runtime.getRuntime();
            
            // CPU 사용률 측정
            double cpuUsage = getCpuUsage(osBean);
            
            // 메모리 사용률 계산
            long maxMemory = runtime.maxMemory();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            double memoryUsage = (double) usedMemory / maxMemory * 100;
            
            return SystemResourceInfo.builder()
                .cpuUsagePercent(Math.round(cpuUsage * 100.0) / 100.0)
                .memoryUsagePercent(Math.round(memoryUsage * 100.0) / 100.0)
                .totalMemoryMB(maxMemory / (1024 * 1024))
                .usedMemoryMB(usedMemory / (1024 * 1024))
                .freeMemoryMB((maxMemory - usedMemory) / (1024 * 1024))
                .availableProcessors(runtime.availableProcessors())
                .build();
                
        } catch (Exception e) {
            log.error("시스템 리소스 정보 수집 실패", e);
            return getDefaultSystemInfo();
        }
    }
}
```

### 성능 등급 자동 판정
```java
public String getPerformanceGrade() {
    if (errorRate > 5.0) {
        return "POOR";           // 에러율 5% 초과
    } else if (averageResponseTime > 1000) {
        return "POOR";           // 평균 응답시간 1초 초과  
    } else if (averageResponseTime > 500) {
        return "FAIR";           // 평균 응답시간 500ms ~ 1초
    } else if (averageResponseTime > 200) {
        return "GOOD";           // 평균 응답시간 200ms ~ 500ms
    } else {
        return "EXCELLENT";      // 평균 응답시간 200ms 이하
    }
}

// 시스템 상태 판정
public String getSystemStatus() {
    if (cpuUsagePercent > 90 || memoryUsagePercent > 90) {
        return "CRITICAL";       // 임계 상태
    } else if (cpuUsagePercent > 70 || memoryUsagePercent > 70) {
        return "WARNING";        // 경고 상태  
    } else if (cpuUsagePercent > 50 || memoryUsagePercent > 50) {
        return "NORMAL";         // 정상 상태
    } else {
        return "OPTIMAL";        // 최적 상태
    }
}
```

## 🛠[설치 및 실행]

### 환경 요구사항
```
Java 21+
Spring Boot 3.4.1
ElasticSearch 8.12.0
Redis 7-alpine
MySQL 8.0+
Docker & Docker Compose
```

### 실행 방법
```bash
# 1. 저장소 클론
git clone https://github.com/your-repo/stocknote-performance.git
cd stocknote-performance

# 2. ElasticSearch & Redis 실행
docker-compose up -d

# 3. 애플리케이션 실행
./gradlew bootRun

# 4. 헬스 체크
curl http://localhost:8080/public/test/health

# 5. 더미 데이터 생성 (선택사항)
curl -X POST "http://localhost:8080/api/v1/performance/generate-dummy-data?postCount=10000&commentCount=50000"

# 6. 성능 테스트 실행
curl -X GET "http://localhost:8080/api/v1/performance/compare-search?keyword=삼성전자&testCount=100"
```

### Docker Compose 설정
```yaml
version: '3.8'

services:
  # ElasticSearch
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.12.0
    container_name: stocknote-elasticsearch
    environment:
      - node.name=stocknote-es
      - cluster.name=stocknote-cluster
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms1g -Xmx1g"
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - es_data:/usr/share/elasticsearch/data

  # Redis  
  redis:
    image: redis:7-alpine
    container_name: stocknote-redis
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes --maxmemory 256mb
    volumes:
      - redis_data:/data

volumes:
  es_data:
    driver: local
  redis_data:
    driver: local
```

## [주요 학습 내용]

### 1. 성능 최적화 경험
- **병목 지점 분석**: 프로파일링 없이 직접 성능 측정 로직 구현
- **검색 엔진 선택**: MySQL LIKE vs ElasticSearch 성능 비교 분석
- **메모리 최적화**: 대용량 데이터 처리 시 배치 처리 및 GC 최적화
- **동시성 처리**: CountDownLatch, AtomicLong 등을 활용한 스레드 안전 구현

### 2. 시스템 설계 능력  
- **아키텍처 분리**: 검색 시스템과 저장 시스템의 역할 분리
- **데이터 동기화**: 실시간 동기화 전략 수립 및 구현
- **확장성 고려**: 마이크로서비스 아키텍처를 고려한 설계
- **모니터링 체계**: 성능 지표 수집 및 분석 시스템 구축

### 3. 테스트 엔지니어링
- **부하 테스트**: 실제 트래픽을 시뮬레이션한 테스트 설계
- **성능 측정**: 정확한 메트릭 수집을 위한 측정 방법론
- **자동화**: 반복 가능한 테스트 환경 구축
- **결과 분석**: 통계적 분석을 통한 성능 개선점 도출

## [성과 및 임팩트]

### 정량적 성과
- **검색 응답시간 98.8% 개선** (244ms → 2.83ms)
- **처리량 8,550% 증가** (408 → 35,335 req/s)  
- **동시 사용자 5배 증가** 지원 (100명 → 500명)
- **시스템 안정성 100%** (에러율 0% 달성)

### 기술적 성과
- **확장 가능한 아키텍처** 구축으로 향후 기능 확장 기반 마련
- **실시간 모니터링 시스템** 구축으로 운영 안정성 확보
- **성능 테스트 자동화**로 지속적 성능 관리 체계 확립
- **대용량 데이터 처리** 경험을 통한 엔터프라이즈급 시스템 개발 역량 확보

## 🔗 관련 링크

- [ElasticSearch 공식 문서](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Spring Data ElasticSearch](https://spring.io/projects/spring-data-elasticsearch)

---

## 📧 Contact

**개발자**: 백성현
**이메일**: ces0135@naver.com

> "성능 최적화는 단순히 빠르게 만드는 것이 아니라, 사용자 경험을 향상시키고 시스템의 확장성을 확보하는 것입니다."


---
# 🗒️ StockNote_BE
> 스톡노트 (주식 포트폴리오 커뮤니티 웹 플랫폼)

총 개발기간 : `2025.01.16` ~ `2025.02.16`
  
</br>
<img width="1629" alt="스크린샷 2025-02-13 오후 5 17 27" src="https://github.com/user-attachments/assets/d4912815-7567-4749-88b7-1461f32f41fd" />

# ⛰️ 팀원 소개
<table>
  <tr>
    <td><img src="https://avatars.githubusercontent.com/u/64017307?v=4" width="200" height="200"></td>
    <td><img src="https://avatars.githubusercontent.com/u/118641096?v=4" width="200" height="200"></td>
    <td><img src="https://avatars.githubusercontent.com/u/181931584?v=4" width="200" height="200"></td>
    <td><img src="https://avatars.githubusercontent.com/u/125850243?v=4" width="200" height="200"></td>
    <td><img src="https://avatars.githubusercontent.com/u/82190411?v=4" width="200" height="200"></td>
  </tr>
  <tr>
    <td><a href="https://github.com/leemimi">이미정</a></td>
    <td><a href="https://github.com/kknaks">이건학</a></td>
    <td><a href="https://github.com/skyeong42">성수경</a></td>
    <td><a href="https://github.com/hohosznta">한유림</a></td>
    <td><a href="https://github.com/ces0135-hub">백성현</a></td>
  </tr>
</table>

## 📚 기술 스택

<img width="1046" alt="스크린샷 2025-02-13 오후 5 18 14" src="https://github.com/user-attachments/assets/af0c6dcd-5de7-493c-9130-a1c39db092af" />
</br>

## 🗺️ 시스템 아키텍쳐

<img width="1545" alt="스크린샷 2025-02-13 오후 5 18 55" src="https://github.com/user-attachments/assets/7c741637-6755-46a6-9340-3f0a8f789025" />
</br>

## ✈️ 인프라 아키텍쳐

<img width="1528" alt="스크린샷 2025-02-13 오후 5 19 24" src="https://github.com/user-attachments/assets/15a0ff76-5629-4473-bcb8-37520cc71f0d" />
</br>

## 📢 엘라스틱서치 아키텍쳐

<img width="1554" alt="스크린샷 2025-02-13 오후 5 20 20" src="https://github.com/user-attachments/assets/1f0da031-1758-4337-b821-0ababc7d01fd" />
</br>

## 📂 ERD

<img width="1502" alt="스크린샷 2025-02-13 오후 5 19 59" src="https://github.com/user-attachments/assets/0bb0989c-a36c-4392-8585-a85180fd2b0b" />
</br>

# 📄 페이지 별 기능

## 📌 메인 페이지  

<img width="1600" alt="메인 페이지" src="https://github.com/user-attachments/assets/5d1c16ab-567e-466b-a917-6eb4141cb7bb" /> 
</br>

### ✅ 기능   
- 실시간 주식 데이터 조회  

## 📌 포트폴리오 관리  

<img width="1600" alt="포트폴리오 관리" src="https://github.com/user-attachments/assets/cb483e44-f555-4be1-ab08-bf25c8ee5b3b" />  

### ✅ 기능  
- 사용자가 보유한 주식 포트폴리오 등록 및 관리  
- 개별 종목 수익률 시각화 및 분석 

## 📌 관심종목 관리

<img width="1600" alt="포트폴리오 관리" src="https://github.com/user-attachments/assets/bec9b82f-ffd0-4573-987b-339e3f181d37" />  

### ✅ 기능  
-  관심종목 실시간 주가 파악
-  종목별 일/월/주/년 별 상세정보 확인
-  매일 업데이트 되는 매수/매도 투표 기능
  

  ## 📌 커뮤니티 

<img width="1600" alt="포트폴리오 관리" src="https://github.com/user-attachments/assets/a627d497-ed85-432d-aba1-8e628c9176ae" />  

### ✅ 기능  
- 커뮤니티 게시글/댓글/좋아요 작성 기능
- 매일 인기글 업데이트
- 인기글/최신글/댓글순/좋아요순 정렬 기능 
- 게시글 제목/작성자/해쉬태그 별 검색 기능 
