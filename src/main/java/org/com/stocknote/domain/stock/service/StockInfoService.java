package org.com.stocknote.domain.stock.service;

import lombok.AllArgsConstructor;
import org.com.stocknote.domain.stock.dto.response.StockInfoResponse;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class StockInfoService {
    private final StockRepository stockRepository;

    //주식종목 검색
    public StockInfoResponse findStock(String name) {
        return stockRepository.findByName(name)
                .map(StockInfoResponse::of)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.STOCK_NOT_FOUND+ name));
    }
}
