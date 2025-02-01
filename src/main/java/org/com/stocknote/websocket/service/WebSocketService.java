package org.com.stocknote.websocket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.global.event.SseEmitters;
import org.com.stocknote.domain.stock.service.price.StockPriceProcessor;
import org.com.stocknote.domain.stockApi.stockToken.service.StockTokenService;
import org.com.stocknote.domain.stockApi.dto.response.StockPriceResponse;
import org.com.stocknote.domain.stockApi.service.StockApiService;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {
    private static final String WEBSOCKET_URL = "ws://ops.koreainvestment.com:21000";
    private static final String TOPIC_PREFIX = "/topic/stocks/";

    private final SimpMessagingTemplate messagingTemplate;
    private final StockApiService stockApiService;
    private final StockPriceProcessor priceProcessor;
    private final StockTokenService stockTokenService;
    private final ObjectMapper objectMapper;
    private final SseEmitters sseEmitters;

    private WebSocketClient client;
    private volatile StockPriceResponse latestPriceResponse;

    /**
     * 주식 가격 구독 시작
     */
    public void subscribeStockPrice(String stockCode) {
        try {
            initializeWebSocketConnection(stockCode);
        } catch (Exception e) {
            log.error("❌ Failed to initialize WebSocket connection for {}: {}", stockCode, e.getMessage());
            fallbackToRestApi(stockCode);
        }
    }

    /**
     * WebSocket 연결 초기화
     */
    private void initializeWebSocketConnection(String stockCode) throws Exception {
        String approvalKey = stockTokenService.getWebSocketApprovalKey();
        latestPriceResponse = null;

        client = createWebSocketClient(stockCode, approvalKey);
        client.connect();
    }

    /**
     * WebSocket 클라이언트 생성
     */
    private WebSocketClient createWebSocketClient(String stockCode, String approvalKey) throws Exception {
        WebSocketClient newClient = new WebSocketClient(new URI(WEBSOCKET_URL)) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                log.info("WebSocket Connected for stock: {}", stockCode);
                sendSubscribeMessage(stockCode);
            }

            @Override
            public void onMessage(String message) {
                handleWebSocketMessage(message, stockCode);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.info("WebSocket Closed for stock {}: {}", stockCode, reason);
            }

            @Override
            public void onError(Exception ex) {
                log.error("❌ WebSocket Error for stock {}: {}", stockCode, ex.getMessage());
                fallbackToRestApi(stockCode);
            }
        };

        newClient.addHeader("approval_key", approvalKey);
        return newClient;
    }

    /**
     * WebSocket 메시지 처리
     */
    private void handleWebSocketMessage(String message, String stockCode) {
        try {
            log.debug("📩 Received WebSocket Message for {}: {}", stockCode, message);
            StockPriceResponse priceResponse = parseStockPriceResponse(message);

            if (priceResponse != null) {
                updateAndNotifyPrice(priceResponse, stockCode);
            }
        } catch (Exception e) {
            log.error("❌ Failed to handle WebSocket message for {}: {}", stockCode, e.getMessage());
        }
    }

    /**
     * 가격 업데이트 및 알림
     */
    private void updateAndNotifyPrice(StockPriceResponse priceResponse, String stockCode) {
        priceProcessor.processStockPriceResponse(priceResponse, stockCode, null, null)
                .ifPresent(stockResponse -> {
                    String destination = TOPIC_PREFIX + stockCode;
                    messagingTemplate.convertAndSend(destination, stockResponse);
                    sseEmitters.sendToAll(stockResponse);
                    log.info("🚀 Price updated for {}: {}", stockCode, stockResponse);
                });
    }

    /**
     * REST API를 통한 폴백 처리
     */
    private void fallbackToRestApi(String stockCode) {
        try {
            StockPriceResponse priceResponse = stockApiService.getStockPrice(stockCode).block();
            if (priceResponse != null) {
                updateAndNotifyPrice(priceResponse, stockCode);
            }
        } catch (Exception e) {
            log.error("❌ Fallback to REST API failed for {}: {}", stockCode, e.getMessage());
        }
    }

    /**
     * 구독 메시지 전송
     */
    private void sendSubscribeMessage(String stockCode) {
        try {
            Map<String, String> header = Map.of("approval_key", stockTokenService.getWebSocketApprovalKey());
            Map<String, Object> message = Map.of(
                    "header", objectMapper.writeValueAsString(header),
                    "type", "stock",
                    "codes", stockCode
            );

            String subscribeMessage = objectMapper.writeValueAsString(message);
            log.debug("📤 Sending subscribe message: {}", subscribeMessage);
            client.send(subscribeMessage);
        } catch (Exception e) {
            log.error("❌ Failed to send subscribe message for {}: {}", stockCode, e.getMessage());
        }
    }

    /**
     * WebSocket 메시지 파싱
     */
    private StockPriceResponse parseStockPriceResponse(String message) {
        try {
            return objectMapper.readValue(message, StockPriceResponse.class);
        } catch (Exception e) {
            log.error("❌ Failed to parse stock price response: {}", e.getMessage());
            return null;
        }
    }

    @PreDestroy
    public void cleanup() {
        if (client != null && !client.isClosed()) {
            client.close();
        }
    }
}
