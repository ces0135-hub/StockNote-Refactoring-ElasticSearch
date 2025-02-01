package org.com.stocknote.domain.portfolio.portfolio.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.com.stocknote.domain.portfolio.portfolioStock.service.PfStockService;
import org.com.stocknote.global.dto.GlobalResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios/{portfolio_no}/cash")
public class CashController {
  private final PortfolioService portfolioService;
  private final PfStockService pfStockService;

  @PostMapping
  public GlobalResponse<String> addCash(@PathVariable("portfolio_no") Long portfolioNo,
      @RequestBody Integer amount) {
    portfolioService.addCash(portfolioNo, amount);
    return GlobalResponse.success("Cash added successfully");
  }

  @PatchMapping
  public GlobalResponse<String> updateCash(@PathVariable("portfolio_no") Long portfolioNo,
      @RequestBody Integer amount) {
    portfolioService.updateCash(portfolioNo, amount);
    return GlobalResponse.success("Cash updated successfully");
  }

  @DeleteMapping
  public GlobalResponse<String> deleteCash(@PathVariable("portfolio_no") Long portfolioNo) {
    portfolioService.deleteCash(portfolioNo);
    return GlobalResponse.success("Cash deleted successfully");
  }
}
