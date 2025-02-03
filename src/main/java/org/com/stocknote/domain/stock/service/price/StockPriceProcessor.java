package org.com.stocknote.domain.stock.service.price;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.memberStock.entity.MemberStock;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stockApi.dto.response.StockPriceResponse;
import org.com.stocknote.domain.stockApi.dto.response.StockResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockPriceProcessor {
    private static final Set<String> loggedMissingStockCodes = ConcurrentHashMap.newKeySet();

    public Optional<StockResponse> processStockPriceResponse(StockPriceResponse priceResponse, String stockCode, Stock stock, MemberStock memberStock) {
        if (priceResponse == null || priceResponse.getOutput() == null) {
            if (loggedMissingStockCodes.add(stockCode)) {
                log.warn("⚠️ No price data available for {}", stockCode);
            }
            return Optional.empty();
        }
        loggedMissingStockCodes.remove(stockCode);

        try {
            StockPriceResponse.Output output = priceResponse.getOutput();
            Long currentPrice = parseLongOrNull(output.getStck_prpr());
            Long openingPrice = parseLongOrNull(output.getStck_oprc());
            String change = calculateChange(currentPrice, openingPrice);
            boolean isPositive = (currentPrice != null && openingPrice != null) ? currentPrice >= openingPrice : false;

            StockResponse stockResponse;
            if (stock != null && memberStock != null) {
                // MemberStock이 있는 경우 (내 주식 목록용)
                stockResponse = StockResponse.of(stock, memberStock);
                stockResponse.updatePriceInfo(currentPrice, change, isPositive);
            } else {
                // WebSocket 구독용
                stockResponse = StockResponse.builder()
                        .code(stockCode)
                        .name(stock != null ? stock.getName() : "")
                        .price(currentPrice)
                        .change(change)
                        .isPositive(isPositive)
                        .build();
            }

            return Optional.of(stockResponse);
        } catch (Exception e) {
            log.error("❌ Failed to process stock price for {}: {}", stockCode, e.getMessage());
            return Optional.empty();
        }
    }

    private Long parseLongOrNull(String value) {
        try {
            return value != null ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String calculateChange(Long currentPrice, Long openingPrice) {
        if (currentPrice == null || openingPrice == null || openingPrice == 0) {
            return "0%";
        }
        double changePercent = ((double) (currentPrice - openingPrice) / openingPrice) * 100;
        return String.format("%.2f%%", changePercent);
    }

    public static void resetLoggedMissingStockCodes () {
        loggedMissingStockCodes.clear();
    }
}
