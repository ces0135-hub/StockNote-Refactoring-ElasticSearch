package org.com.stocknote.domain.stock.entity;

import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.stock.dto.response.StockPriceResponse;
import org.com.stocknote.domain.stock.dto.response.StockResponse;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StockPriceEventListener {
    private final SseEmitters sseEmitters;  // SSE 연결을 관리하는 클래스 필요

    public StockPriceEventListener(SseEmitters sseEmitters) {
        this.sseEmitters = sseEmitters;
    }

    @EventListener
    public void handleStockPriceUpdate(StockPriceUpdateEvent event) {
        StockPriceResponse priceResponse = event.getPriceResponse();
        sseEmitters.sendToAll(priceResponse);  // 모든 연결된 클라이언트에게 전송
    }
}
