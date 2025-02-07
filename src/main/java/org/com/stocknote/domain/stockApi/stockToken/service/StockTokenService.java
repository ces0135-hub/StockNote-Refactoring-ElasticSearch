package org.com.stocknote.domain.stockApi.stockToken.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Getter
@Component
public class StockTokenService {
    private final StringRedisTemplate redisTemplate;
    private WebClient tokenWebClient;
    private WebClient websocketWebClient;
    private String accessToken;
    private LocalDateTime tokenExpirationTime;
    private String websocketApprovalKey;
    private final ReentrantLock lock = new ReentrantLock();

    @Value("${kis.app-key}")
    private String appKey;

    @Value("${kis.app-secret}")
    private String appSecret;

    @Value("${kis.index-base-url}")
    private String indexBaseUrl;

    @Value("${kis.token-base-url}")
    private String tokenBaseUrl;

    @Value("${kis.volume-base-url}")
    private String volumeBaseUrl;

    public StockTokenService (StringRedisTemplate redisTemplate, WebClient.Builder webClientBuilder) {
        this.redisTemplate = redisTemplate;
        this.tokenWebClient = webClientBuilder.baseUrl("https://openapi.koreainvestment.com:9443").build();
    }

    @PostConstruct
    public void initWebClient() {
        this.tokenWebClient = WebClient.builder()
                .baseUrl(tokenBaseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.websocketWebClient = WebClient.builder()
                .baseUrl("https://openapi.koreainvestment.com:9443")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    //@Cacheable(value = "accessToken", unless = "#result == null")

    public String getAccessToken() {
        String cachedToken = redisTemplate.opsForValue().get("stocknote:access-token");

        if (cachedToken != null) {
            return cachedToken;  // ✅ Redis에서 저장된 토큰 사용
        }

        if (!lock.tryLock()) {
            return accessToken;  // ✅ 다른 요청이 토큰을 갱신 중이면 기존 토큰 사용
        }

        try {
            return refreshAccessToken();  // ✅ 새 토큰 요청 후 반환
        } finally {
            lock.unlock();
        }
    }


    private boolean isTokenExpired() {
        return tokenExpirationTime == null || LocalDateTime.now().isAfter(tokenExpirationTime);
    }

    private String refreshAccessToken() {
        Map<String, Object> response = tokenWebClient
                .post()
                .uri("/oauth2/tokenP")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "grant_type", "client_credentials",
                        "appkey", appKey,
                        "appsecret", appSecret
                ))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();  // ✅ 동기 호출로 변경 (즉시 토큰 적용)

        if (response != null && response.get("access_token") != null) {
            this.accessToken = (String) response.get("access_token");
            long expiresIn = ((Number) response.get("expires_in")).longValue();
            this.tokenExpirationTime = LocalDateTime.now().plusSeconds(expiresIn);

            // ✅ Redis에 저장하여 캐싱
            redisTemplate.opsForValue().set("stocknote:access-token", accessToken, expiresIn - 10, TimeUnit.SECONDS);

            return accessToken;
        } else {
            throw new RuntimeException("Access Token 발급 응답이 비어있습니다.");
        }
    }

    public synchronized String getWebSocketApprovalKey() {
        if (websocketApprovalKey == null) {
            requestWebSocketApprovalKey();
        }
        return websocketApprovalKey;
    }

    private void requestWebSocketApprovalKey() {
        try {
            Map<String, Object> response = websocketWebClient
                    .post()
                    .uri("/oauth2/Approval")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "grant_type", "client_credentials",
                            "appkey", appKey,
                            "secretkey", appSecret
                    ))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("WebSocket approval key request failed: {}", errorBody);
                                        return Mono.error(new RuntimeException("WebSocket approval key 발급 실패: " + errorBody));
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response != null && response.get("approval_key") != null) {
                this.websocketApprovalKey = (String) response.get("approval_key");
                log.debug("WebSocket approval key generated successfully");
            } else {
                throw new RuntimeException("WebSocket approval key 발급 응답이 비어있습니다.");
            }
        } catch (Exception e) {
            log.error("WebSocket approval key request failed", e);
            throw new RuntimeException("WebSocket approval key 발급 실패: " + e.getMessage(), e);
        }
    }
}
