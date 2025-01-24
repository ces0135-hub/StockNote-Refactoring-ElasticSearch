package org.com.stocknote.domain.portfolio.portfolioStock.controller;


import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.portfolioStock.service.PfStockService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios/{portfolio_no}/stocks")
public class PfStockController {
  private final PfStockService pfStockService;

  @PostMapping
  public String postPortfolioStock() {
    return "Portfolio post";
  }

  @PatchMapping
  public String patchPortfolioStock() {
    return "Portfolio modify";
  }

  @DeleteMapping
  public String deletePortfolioStock() {
    return "Portfolio del";
  }
}
