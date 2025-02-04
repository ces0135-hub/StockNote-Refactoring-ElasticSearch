package org.com.stocknote.domain.stockApi.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.com.stocknote.domain.stock.entity.Stock;

@Data
@Getter
@Builder
public class StockInfoResponse {
    private String code; //종목코드
    private String name; //종목명
    private String market; //시장구분


    public static StockInfoResponse of (Stock stock){
        return StockInfoResponse.builder()
                .name(stock.getName())
                .code(stock.getCode())
                .market(stock.getMarket())
                .build();
    }
}
