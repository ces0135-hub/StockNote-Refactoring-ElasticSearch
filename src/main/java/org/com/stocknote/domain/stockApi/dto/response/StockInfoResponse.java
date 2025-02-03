package org.com.stocknote.domain.stockApi.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.com.stocknote.domain.stock.entity.Stock;

@Data
@Getter
@Builder
public class StockInfoResponse {
    private String name;
    private String code;

    public static StockInfoResponse of (Stock stock){
        return StockInfoResponse.builder()
                .name(stock.getName())
                .code(stock.getCode())
                .build();
    }
}
