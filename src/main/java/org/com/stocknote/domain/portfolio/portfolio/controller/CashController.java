package org.com.stocknote.domain.portfolio.portfolio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios/{portfolio_no}/cash")
@Tag(name = "포트폴리오 현금 API", description = "포트폴리오 현금 API")
public class CashController {
  private final PortfolioService portfolioService;

  @PostMapping
  @Operation(summary = "현금 추가")
  public GlobalResponse<String> addCash(@PathVariable("portfolio_no") Long portfolioNo,
                                        @RequestBody Integer amount) {
    portfolioService.addCash(portfolioNo, amount);
    return GlobalResponse.success("Cash added successfully");
  }

  @PatchMapping
  @Operation(summary = "현금 수정")
  public GlobalResponse<String> updateCash(@PathVariable("portfolio_no") Long portfolioNo,
      @RequestBody Integer amount) {
    portfolioService.updateCash(portfolioNo, amount);
    return GlobalResponse.success("Cash updated successfully");
  }

  @DeleteMapping
  @Operation(summary = "현금 삭제")
  public GlobalResponse<String> deleteCash(@PathVariable("portfolio_no") Long portfolioNo) {
    portfolioService.deleteCash(portfolioNo);
    return GlobalResponse.success("Cash deleted successfully");
  }
}
