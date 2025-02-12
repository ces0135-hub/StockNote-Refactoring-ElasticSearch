package org.com.stocknote.domain.hashtagAutocomplete;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Range;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSortedSetService {
    private final StringRedisTemplate redisTemplate;
    private static final String KEY = "autocorrect";
    private static final int MAX_RESULTS = 10;
    private static final String SUFFIX = "*"; // 완전한 단어 구분

    public void addAllToSortedSet(List<String> values) {
        try {
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);

                for (String value : values) {
                    byte[] fullValueBytes = (value + SUFFIX).getBytes(StandardCharsets.UTF_8);
                    connection.zAdd(keyBytes, 0, fullValueBytes);

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
    public List<String> autocomplete(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String searchKeyword = keyword.trim();
        String min = searchKeyword;
        String max = searchKeyword + "\uffff";

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

    public void clearAll() {
        try {
            redisTemplate.delete(KEY);
        } catch (Exception e) {
            log.error("Error clearing Redis: {}", e.getMessage());
            throw new RuntimeException("Failed to clear Redis", e);
        }
    }
}
