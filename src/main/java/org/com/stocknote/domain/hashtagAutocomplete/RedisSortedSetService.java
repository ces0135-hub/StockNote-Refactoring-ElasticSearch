package org.com.stocknote.domain.hashtagAutocomplete;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.Prefix;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Range;


import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

//@Service
//@RequiredArgsConstructor
//public class RedisSortedSetService {    //검색어 자동 완성을 구현할 때 사용하는 Redis의 SortedSet 관련 서비스 레이어
//    private final StringRedisTemplate redisTemplate;
//    private String key = "autocorrect"; //검색어 자동 완성을 위한 Redis 데이터
//    private int score = 0;  //Score는 딱히 필요 없으므로 하나로 통일
//
//    public void addToSortedSet(String value) {    //Redis SortedSet에 추가
//        redisTemplate.opsForZSet().add(key, value, score);
//    }
//
//    public Long findFromSortedSet(String value) {   //Redis SortedSet에서 Value를 찾아 인덱스를 반환
//        return redisTemplate.opsForZSet().rank(key, value);
//    }
//
//    public Set<String> findAllValuesAfterIndexFromSortedSet(Long index) {
//        return redisTemplate.opsForZSet().range(key, index, index + 10);   //전체를 다 불러오기 보다는 200개 정도만 가져와도 자동 완성을 구현하는 데 무리가 없으므로 200개로 rough하게 설정
//    }
//}
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSortedSetService {
    private final StringRedisTemplate redisTemplate;
    private static final String KEY = "autocorrect";
    private static final int MAX_RESULTS = 10;
    private static final String SUFFIX = "*"; // 완전한 단어 구분

    /**
     * 여러 단어를 한 번에 Redis Sorted Set에 추가
     */
    public void addAllToSortedSet(List<String> values) {
        try {
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);

                for (String value : values) {
                    byte[] fullValueBytes = (value + SUFFIX).getBytes(StandardCharsets.UTF_8);
                    connection.zAdd(keyBytes, 0, fullValueBytes);

                    // 모든 부분 문자열 저장
                    for (int i = 1; i <= value.length(); i++) {
                        byte[] substringBytes = value.substring(0, i).getBytes(StandardCharsets.UTF_8);
                        connection.zAdd(keyBytes, 0, substringBytes);
                    }
                }
                return null;
            });
        } catch (Exception e) {
            log.error("Error in batch add: {}", e.getMessage());
            throw new RuntimeException("Failed to add values to Redis", e);
        }
    }
     // Prefix 기반 자동완성 검색 (Redis 6.2+ : ZRANGE BYLEX 사용)
    public List<String> autocomplete(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String searchKeyword = keyword.trim();
        String min = searchKeyword;  // 검색어 최소값 (시작점)
        String max = searchKeyword + "\uffff"; // 최대값 (해당 prefix로 시작하는 모든 값)

        try {
            List<String> results = redisTemplate.opsForZSet()
                    .rangeByLex(KEY, Range.rightOpen(min, max))
                    .stream()
                    .filter(value -> value.endsWith(SUFFIX)) // 완전한 단어만 필터링
                    .map(value -> value.replace(SUFFIX, ""))
                    .limit(MAX_RESULTS)
                    .toList();

            return results;
        } catch (Exception e) {
            log.error("Error in autocomplete: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Redis 데이터 초기화
     */
    public void clearAll() {
        try {
            redisTemplate.delete(KEY);
        } catch (Exception e) {
            log.error("Error clearing Redis: {}", e.getMessage());
            throw new RuntimeException("Failed to clear Redis", e);
        }
    }
}



