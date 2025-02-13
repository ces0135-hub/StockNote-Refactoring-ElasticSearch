package org.com.stocknote.domain.searchDoc.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.com.stocknote.domain.searchDoc.document.StockDoc;

@Data
@Getter
@Builder
public class SearchedStockResponse {
    private String code; //종목코드
    private String name; //종목명
    private String market; //시장구분


    public static SearchedStockResponse of (StockDoc stockDoc){
        return SearchedStockResponse.builder()
                .name(stockDoc.getName())
                .code(stockDoc.getCode())
                .market(stockDoc.getMarket())
                .build();
    }
}
