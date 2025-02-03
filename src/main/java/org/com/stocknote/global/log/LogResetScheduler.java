package org.com.stocknote.global.log;

import lombok.AllArgsConstructor;
import org.com.stocknote.domain.stock.service.price.StockPriceProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LogResetScheduler {
    private final StockPriceProcessor stockPriceProcessor;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void resetLoggedMissingStockCodes() {
        StockPriceProcessor.resetLoggedMissingStockCodes();
    }
}
