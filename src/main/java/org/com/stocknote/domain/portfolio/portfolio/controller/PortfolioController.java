package org.com.stocknote.domain.portfolio.portfolio.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.portfolio.dto.PortfolioResponse;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.portfolio.portfolioStock.service.PfStockService;
import org.com.stocknote.global.dto.GlobalResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios")
public class PortfolioController {
  private final PortfolioService portfolioService;
  private final PfStockService pfStockService;

  @GetMapping
  public GlobalResponse<List<PortfolioResponse>> getPortfolioList() {
    List<Portfolio> portfolio = portfolioService.getPortfolioList();
    List<PortfolioResponse> response = portfolio.stream()
        .map(PortfolioResponse::from)
        .collect(Collectors.toList());
    return GlobalResponse.success(response);
  }

  @GetMapping("/{portfolio_no}")
  public String getPortfolioStock(
      @PathVariable("portfolio_no") Long portfolioNo) {
    List<PfStock> pfStockList = pfStockService.getStockList(portfolioNo);
    return "Portfolio get";
  }

  @PostMapping
  public String postPortfolioList() {
    return "PortfolioList post";
  }

  @PatchMapping
  public String patchPortfolioList() {
    return "PortfolioList modify";
  }

  @DeleteMapping
  public String deletePortfolioList() {

    return "PortfolioList del";
  }
}
