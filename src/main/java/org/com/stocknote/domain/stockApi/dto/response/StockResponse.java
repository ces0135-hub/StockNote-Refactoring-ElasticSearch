package org.com.stocknote.domain.stockApi.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.stocknote.domain.memberStock.entity.MemberStock;
import org.com.stocknote.domain.stock.entity.Stock;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockResponse {
    private String code;
    private String name;
    private Long price;
    private String change;
    private boolean isPositive;
    private LocalDateTime addedAt;

    @Builder
    private StockResponse(String code, String name, Long price,
                          String change, boolean isPositive,
                          LocalDateTime addedAt) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.change = change;
        this.isPositive = isPositive;
        this.addedAt = addedAt;
    }

    public static StockResponse of(Stock stock, MemberStock memberStock) {
        return StockResponse.builder()
                .code(stock.getCode())
                .name(stock.getName())
                .price(null)
                .change(null)
                .isPositive(false)
                .addedAt(memberStock.getAddedAt())
                .build();
    }

    // 실시간 가격 업데이트 메서드 추가
    public void updatePriceInfo(Long price, String change, boolean isPositive) {
        this.price = price;
        this.change = change;
        this.isPositive = isPositive;
    }
}
