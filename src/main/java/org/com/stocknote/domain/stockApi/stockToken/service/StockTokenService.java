package org.com.stocknote.domain.stockApi.stockToken.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.stockApi.stockToken.dto.StockTokenRequestDto;
import org.com.stocknote.domain.stockApi.stockToken.dto.StockTokenResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Getter
@Component
public class StockTokenService {
    private WebClient tokenWebClient;
    private WebClient websocketWebClient;
    private String accessToken;
    private LocalDateTime tokenExpirationTime;
    private String websocketApprovalKey;
    private String cachedToken;
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

        this.cachedToken = generateNewToken();
    }

    @Cacheable(value = "accessToken", unless = "#result == null")
    public String getAccessToken() {
        if (accessToken == null || isTokenExpired()) {
            requestNewAccessToken();
        }
        return accessToken;
    }

    private boolean isTokenExpired() {
        return tokenExpirationTime == null || LocalDateTime.now().isAfter(tokenExpirationTime);
    }

    private void requestNewAccessToken() {
        try {
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
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Token request failed: {}", errorBody);
                                        return Mono.error(new RuntimeException("토큰 발급 실패: " + errorBody));
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response != null) {
                this.accessToken = (String) response.get("access_token");
                long expiresIn = ((Number) response.get("expires_in")).longValue();
                this.tokenExpirationTime = LocalDateTime.now().plusSeconds(expiresIn);
                log.debug("New access token generated, expires in {} seconds", expiresIn);
            } else {
                throw new RuntimeException("토큰 발급 응답이 비어있습니다.");
            }
        } catch (Exception e) {
            log.error("Access Token 발급 실패", e);
            throw new RuntimeException("Access Token 발급 실패: " + e.getMessage(), e);
        }
    }

    public String generateNewToken() {
        try {
            StockTokenRequestDto stockTokenRequestDto = new StockTokenRequestDto("client_credentials", appKey, appSecret);

            StockTokenResponseDto response = tokenWebClient
                    .post()
                    .uri("/oauth2/tokenP")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(stockTokenRequestDto)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Token generation failed: {}", errorBody);
                                        return Mono.error(new RuntimeException("새로운 토큰 발급 실패: " + errorBody));
                                    })
                    )
                    .bodyToMono(StockTokenResponseDto.class)
                    .block();

            if (response != null) {
                log.debug("New token generated successfully");
                return response.getAccessToken();
            } else {
                throw new RuntimeException("토큰 발급 응답이 비어있습니다.");
            }
        } catch (Exception e) {
            log.error("Token generation failed", e);
            throw new RuntimeException("토큰 발급 실패: " + e.getMessage(), e);
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
