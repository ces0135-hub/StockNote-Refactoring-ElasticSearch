package org.com.stocknote.domain.stockApi.kis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.stock.dto.response.StockPriceResponse;
import org.com.stocknote.domain.stock.dto.response.StockResponse;
import org.com.stocknote.domain.stock.service.StockService;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.net.URI;

@Service
@Slf4j
@AllArgsConstructor
public class WebSocketClientService {

    private final KisKeyManager keyManager;
    private final SimpMessagingTemplate messagingTemplate;
    private WebSocketClient client;
    private StockPriceResponse latestPriceResponse;

    @Autowired
    public WebSocketClientService(KisKeyManager keyManager, SimpMessagingTemplate messagingTemplate) {
        this.keyManager = keyManager;
        this.messagingTemplate = messagingTemplate;
        this.client = null;
        this.latestPriceResponse = null;
    }

    /**
     * 주식 가격 정보를 조회하고 WebSocket을 통해 전송
     */
    public void subscribeStockPrice(String stockCode) {
        String destination = "/topic/stocks/" + stockCode;

        try {
            // 실시간 가격 조회
            StockPriceResponse priceResponse = fetchRealTimeStockPrice(stockCode);

            if (priceResponse != null && priceResponse.getOutput() != null) {
                StockPriceResponse.Output output = priceResponse.getOutput();

                Long currentPrice = parseLongOrNull(output.getStck_prpr()); // 현재가
                Long openingPrice = parseLongOrNull(output.getStck_oprc()); // 시가
                String change = calculateChange(currentPrice, openingPrice);
                boolean isPositive = (currentPrice != null && openingPrice != null) ? currentPrice >= openingPrice : false;

                // StockResponse 객체 생성
                StockResponse stockResponse = StockResponse.builder()
                        .code(stockCode)
                        .name("")  // 종목명은 따로 설정 필요
                        .price(currentPrice)
                        .change(change)
                        .isPositive(isPositive)
                        .addedAt(null)  // 필요 시 설정
                        .build();

                log.info("🚀 Sending WebSocket message with price data to {}: {}", destination, stockResponse);
                messagingTemplate.convertAndSend(destination, stockResponse);
            } else {
                log.warn("⚠️ No price data available for {}", stockCode);
            }
        } catch (Exception e) {
            log.error("❌ Failed to fetch stock price for {}: {}", stockCode, e.getMessage());
        }
    }

    /**
     * 실시간 주식 가격을 가져오는 함수
     */
    private StockPriceResponse fetchRealTimeStockPrice(String stockCode) {
        try {
            String approvalKey = keyManager.getWebSocketApprovalKey();

            client = new WebSocketClient(new URI("ws://ops.koreainvestment.com:21000")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("WebSocket Connected: {}", stockCode);
                    sendSubscribeMessage(stockCode);
                }

                @Override
                public void onMessage(String message) {
                    log.info("📩 Received WebSocket Message for {}: {}", stockCode, message);
                    latestPriceResponse = parseStockPriceResponse(message);
                    close(); // WebSocket 연결 종료
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("WebSocket Closed: {}", reason);
                }

                @Override
                public void onError(Exception ex) {
                    log.error("❌ WebSocket Error: {}", ex.getMessage());
                }
            };

            client.addHeader("approval_key", approvalKey);
            client.connect();

            while (latestPriceResponse == null) {
                Thread.sleep(100);
            }

            return latestPriceResponse;

        } catch (Exception e) {
            log.error("❌ Failed to fetch real-time stock price: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 주식 데이터 구독 메시지 전송
     */
    private void sendSubscribeMessage(String stockCode) {
        JsonObject message = new JsonObject();
        message.addProperty("header", "{\"approval_key\":\"" + keyManager.getWebSocketApprovalKey() + "\"}");
        message.addProperty("type", "stock");
        message.addProperty("codes", stockCode);
        log.info("📤 Sending subscribe message: {}", message.toString());
        client.send(message.toString());
    }

    /**
     * WebSocket에서 받은 JSON 데이터를 StockPriceResponse로 변환
     */
    private StockPriceResponse parseStockPriceResponse(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(message);
            StockPriceResponse priceResponse = new StockPriceResponse();
            StockPriceResponse.Output output = new StockPriceResponse.Output();

            if (node.has("output")) {
                JsonNode outputNode = node.get("output");
                if (outputNode.has("stck_prpr")) {
                    output.setStck_prpr(outputNode.get("stck_prpr").asText());
                }
                if (outputNode.has("stck_oprc")) {
                    output.setStck_oprc(outputNode.get("stck_oprc").asText());
                }
            }

            priceResponse.setOutput(output);
            return priceResponse;
        } catch (Exception e) {
            log.error("❌ Failed to parse stock price response: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 변동률 계산 함수
     */
    private String calculateChange(Long currentPrice, Long openingPrice) {
        if (currentPrice == null || openingPrice == null || openingPrice == 0) {
            return "-";
        }
        double changePercent = ((double) (currentPrice - openingPrice) / openingPrice) * 100;
        return String.format("%.2f%%", changePercent);
    }

    /**
     * 문자열을 안전하게 Long으로 변환하는 함수
     */
    private Long parseLongOrNull(String value) {
        try {
            return value != null ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            log.error("❌ Failed to parse Long value from string: {}", value);
            return null;
        }
    }
}
