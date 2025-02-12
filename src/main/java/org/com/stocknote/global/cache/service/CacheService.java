package org.com.stocknote.global.cache.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String POPULAR_POSTS_CACHE_KEY = "popularPosts";

    public void clearPopularPostsCache() {
        redisTemplate.delete(POPULAR_POSTS_CACHE_KEY);
    }
}
