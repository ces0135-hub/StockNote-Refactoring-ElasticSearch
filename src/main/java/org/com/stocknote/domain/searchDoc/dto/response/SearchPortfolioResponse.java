package org.com.stocknote.domain.searchDoc.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.com.stocknote.domain.searchDoc.document.PortfolioDoc;
import org.com.stocknote.domain.searchDoc.document.PortfolioStockDoc;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SearchPortfolioResponse {
  private String id;
  private String category;
  private String name;
  private String description;
  private int totalAsset;
  private int cash;
  private int totalProfit;
  private int totalStock;
  private List<SearchPortfolioStockResponse> pfStocks;

  public static SearchPortfolioResponse from(
      PortfolioDoc portfolioDoc,
      List<PortfolioStockDoc> portfolioStockDocList) {
    return SearchPortfolioResponse.builder()
        .id(portfolioDoc.getId())
        .name("전체 포트폴리오")
        .description("전체 포트폴리오")
        .totalAsset(portfolioDoc.getTotalAsset())
        .cash(portfolioDoc.getTotalCash())
        .totalProfit(portfolioDoc.getTotalProfit())
        .totalStock(portfolioDoc.getTotalStock())
        .pfStocks(portfolioStockDocList.stream().map(SearchPortfolioStockResponse::from).collect(Collectors.toList()))
        .build();
  }
}
