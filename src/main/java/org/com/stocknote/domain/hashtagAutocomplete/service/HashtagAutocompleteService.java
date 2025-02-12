package org.com.stocknote.domain.hashtagAutocomplete.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.com.stocknote.domain.hashtagAutocomplete.RedisSortedSetService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

//@Service
//@Transactional(readOnly = true)
//@RequiredArgsConstructor
//public class HashtagAutocompleteService {
//    private final StockRepository stockRepository;
//    private final RedisSortedSetService redisSortedSetService;
//    private String suffix = "*";    //검색어 자동 완성 기능에서 실제 노출될 수 있는 완벽한 형태의 단어를 구분하기 위한 접미사
//    private int maxSize = 10;    //검색어 자동 완성 기능 최대 개수
//
//    @PostConstruct
//    public void init() {    //이 Service Bean이 생성된 이후에 검색어 자동 완성 기능을 위한 데이터들을 Redis에 저장 (Redis는 인메모리 DB라 휘발성을 띄기 때문)
//        saveAllSubstring(stockRepository.findAllName()); //MySQL DB에 저장된 모든 가게명을 음절 단위로 잘라 모든 Substring을 Redis에 저장해주는 로직
//    }
//
//    private void saveAllSubstring(List<String> allDisplayName) { //MySQL DB에 저장된 모든 가게명을 음절 단위로 잘라 모든 Substring을 Redis에 저장해주는 로직
//        // long start1 = System.currentTimeMillis(); //뒤에서 성능 비교를 위해 시간을 재는 용도
//        for (String displayName : allDisplayName) {
//            redisSortedSetService.addToSortedSet(displayName + suffix);   //완벽한 형태의 단어일 경우에는 *을 붙여 구분
//
//            for (int i = displayName.length(); i > 0; --i) { //음절 단위로 잘라서 모든 Substring 구하기
//                redisSortedSetService.addToSortedSet(displayName.substring(0, i)); //곧바로 redis에 저장
//            }
//        }
//        // long end1 = System.currentTimeMillis(); //뒤에서 성능 비교를 위해 시간을 재는 용도
//        // long elapsed1 = end1 - start1;  //뒤에서 성능 비교를 위해 시간을 재는 용도
//    }
//
//    public List<String> autocorrect(String keyword) { //검색어 자동 완성 기능 관련 로직
//        Long index = redisSortedSetService.findFromSortedSet(keyword);  //사용자가 입력한 검색어를 바탕으로 Redis에서 조회한 결과 매칭되는 index
//
//        if (index == null) {
//            return new ArrayList<>();   //만약 사용자 검색어 바탕으로 자동 완성 검색어를 만들 수 없으면 Empty Array 리턴
//        }
//
//        Set<String> allValuesAfterIndexFromSortedSet = redisSortedSetService.findAllValuesAfterIndexFromSortedSet(index);   //사용자 검색어 이후로 정렬된 Redis 데이터들 가져오기
//
//        List<String> autocorrectKeywords = allValuesAfterIndexFromSortedSet.stream()
//                .filter(value -> value.endsWith(suffix) && value.startsWith(keyword))
//                .map(value -> StringUtils.removeEnd(value, suffix))
//                .limit(maxSize)
//                .toList();  //자동 완성을 통해 만들어진 최대 maxSize개의 키워드들
//
//        return autocorrectKeywords;
//    }
//}

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HashtagAutocompleteService {
    private final StockRepository stockRepository;
    private final RedisSortedSetService redisSortedSetService;
    private static final int MAX_SIZE = 10;

    @PostConstruct
    public void init() {
        // 🔹 Redis 초기화
        redisSortedSetService.clearAll();

        // 🔹 MySQL에서 데이터 가져와서 Redis에 저장
        List<String> allNames = stockRepository.findAllName();
        if (allNames != null && !allNames.isEmpty()) {
            redisSortedSetService.addAllToSortedSet(allNames);  // ✅ `addAllToSortedSet()` 사용
        }
    }

    public List<String> autocomplete(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 🔹 키워드 기반 자동완성 (빠른 Redis 조회)
        return redisSortedSetService.autocomplete(keyword.trim());
    }
}


