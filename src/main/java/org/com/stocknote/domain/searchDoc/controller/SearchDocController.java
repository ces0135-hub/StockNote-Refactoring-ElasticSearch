package org.com.stocknote.domain.searchDoc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.portfolio.dto.response.PortfolioResponse;
import org.com.stocknote.domain.searchDoc.document.PortfolioDoc;
import org.com.stocknote.domain.searchDoc.document.PortfolioStockDoc;
import org.com.stocknote.domain.searchDoc.document.StockDoc;
import org.com.stocknote.domain.searchDoc.dto.request.SearchKeyword;
import org.com.stocknote.domain.searchDoc.dto.response.SearchPortfolioResponse;
import org.com.stocknote.domain.searchDoc.dto.response.SearchedStockResponse;
import org.com.stocknote.domain.searchDoc.service.StockDocService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/searchDocs")
@Tag(name = "검색 API", description = "검색(Search)")
public class SearchDocController {
  private final StockDocService stockDocService;

  @PostMapping("/stock")
  @Operation(summary = "종목 조회")
  public GlobalResponse<List<SearchedStockResponse>> searchStocks(
      @RequestBody SearchKeyword searchKeyword
      ) {
    List<StockDoc> stockList = stockDocService.searchStocks(searchKeyword.getKeyword());
    List<SearchedStockResponse> response =
        stockList.stream().map(SearchedStockResponse::of).collect(Collectors.toList());
    return GlobalResponse.success(response);
  }

  @GetMapping("/myPortfolio")
  @Operation(summary = "포트폴리오 몰아보기")
  public GlobalResponse<SearchPortfolioResponse> getMyPortfolioList(
      @AuthenticationPrincipal PrincipalDetails principalDetails
  ) {
    String email = principalDetails.getUsername();
    PortfolioDoc portfolioDoc = stockDocService.getMyPortfolioList(email);
    List<PortfolioStockDoc> portfolioStockDocList = stockDocService.getMyPortfolioStockList(email);
    SearchPortfolioResponse
        response = SearchPortfolioResponse.from(portfolioDoc, portfolioStockDocList);
    return GlobalResponse.success(response);

  }
}
