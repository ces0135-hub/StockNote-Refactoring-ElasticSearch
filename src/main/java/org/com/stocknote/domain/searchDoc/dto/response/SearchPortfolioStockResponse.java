package org.com.stocknote.domain.searchDoc.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.com.stocknote.domain.searchDoc.document.PortfolioStockDoc;

@Getter
@Builder
public class SearchPortfolioStockResponse {
  private Long id;
  private int pfstockCount;
  private int pfstockPrice;
  private int pfstockTotalPrice;
  private int currentPrice;
  private String market; //시장구분
  private String idxBztpSclsCdName; //종목소분류
  private String stockName;
  private String stockCode;

  public static SearchPortfolioStockResponse from(PortfolioStockDoc pfStockDoc) {
    return SearchPortfolioStockResponse.builder()
        .id(Long.valueOf(pfStockDoc.getId()))
        .pfstockCount(pfStockDoc.getPfstockCount())
        .pfstockPrice(pfStockDoc.getPfstockPrice())
        .pfstockTotalPrice(pfStockDoc.getPfstockTotalPrice())
        .currentPrice(pfStockDoc.getCurrentPrice())
        .market(pfStockDoc.getStockDoc().getMarket())
        .idxBztpSclsCdName(pfStockDoc.getIdxBztpSclsCdName())
        .stockName(pfStockDoc.getStockDoc().getName())
        .stockCode(pfStockDoc.getStockDoc().getCode())
        .build();
  }
}
