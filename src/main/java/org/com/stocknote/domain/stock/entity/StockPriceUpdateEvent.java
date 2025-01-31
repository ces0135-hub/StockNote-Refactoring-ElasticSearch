package org.com.stocknote.domain.stock.entity;

import lombok.Getter;
import org.com.stocknote.domain.stock.dto.response.StockPriceResponse;
import org.com.stocknote.domain.stock.dto.response.StockResponse;

import java.time.LocalDateTime;

@Getter
public class StockPriceUpdateEvent {
    private final String stockCode;
    private final StockPriceResponse priceResponse;
    private final LocalDateTime timestamp;

    public StockPriceUpdateEvent(String stockCode, StockPriceResponse priceResponse) {
        this.stockCode = stockCode;
        this.priceResponse = priceResponse;
        this.timestamp = LocalDateTime.now();
    }
}
