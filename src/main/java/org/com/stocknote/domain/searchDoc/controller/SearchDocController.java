package org.com.stocknote.domain.searchDoc.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.searchDoc.document.StockDoc;
import org.com.stocknote.domain.searchDoc.dto.request.SearchKeyword;
import org.com.stocknote.domain.searchDoc.dto.response.SearchedStockResponse;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stockApi.dto.response.StockInfoResponse;
import org.com.stocknote.domain.searchDoc.service.StockDocService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
