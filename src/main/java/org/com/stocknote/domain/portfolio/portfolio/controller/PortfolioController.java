package org.com.stocknote.domain.portfolio.portfolio.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.service.MemberService;
import org.com.stocknote.domain.portfolio.portfolio.dto.request.PortfolioPatchRequest;
import org.com.stocknote.domain.portfolio.portfolio.dto.request.PortfolioRequest;
import org.com.stocknote.domain.portfolio.portfolio.dto.response.PortfolioResponse;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.response.PfStockResponse;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.portfolio.portfolioStock.service.PfStockService;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.global.dto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios")
public class PortfolioController {
  private final PortfolioService portfolioService;

  @GetMapping
  public GlobalResponse<List<PortfolioResponse>> getPortfolioList(
      @AuthenticationPrincipal PrincipalDetails principalDetails
  ) {
    String email = principalDetails.getUsername();
    List<Portfolio> portfolio = portfolioService.getPortfolioList(email);
    List<PortfolioResponse> response =
        portfolio.stream().map(PortfolioResponse::from).collect(Collectors.toList());
    return GlobalResponse.success(response);
  }

  @GetMapping("/my")
  public GlobalResponse<PortfolioResponse> getMyPortfolioList(
      @AuthenticationPrincipal PrincipalDetails principalDetails
  ) {
    String email = principalDetails.getUsername();
    Portfolio portfolio = portfolioService.getPortfolioListByMember(email);
    PortfolioResponse response = PortfolioResponse.from(portfolio);
    return GlobalResponse.success(response);
  }

  @GetMapping("/{portfolio_no}")
  public GlobalResponse<PortfolioResponse> getPortfolioStock(
      @PathVariable("portfolio_no") Long portfolioNo) {
    Portfolio portfolio = portfolioService.getPortfolio(portfolioNo);
    PortfolioResponse response = PortfolioResponse.from(portfolio);
    return GlobalResponse.success(response);
  }

  @PostMapping
  public GlobalResponse<String> addPortfolio(
      @RequestBody PortfolioRequest portfolioRequest,
      @AuthenticationPrincipal PrincipalDetails principalDetails)
  {
    String email = principalDetails.getUsername();
    portfolioService.save(portfolioRequest, email);
    return GlobalResponse.success("PortfolioList post");
  }

  @PatchMapping("/{portfolio_no}")
  public GlobalResponse<String> updatePortfolio(@PathVariable("portfolio_no") Long portfolioNo,
      @Valid @RequestBody PortfolioPatchRequest request) {
    portfolioService.update(portfolioNo, request);
    return GlobalResponse.success("Portfolio updated successfully");
  }

  @DeleteMapping("/{portfolio_no}")
  public GlobalResponse<String> deletePortfolio(@PathVariable("portfolio_no") Long portfolioNo) {
    portfolioService.delete(portfolioNo);
    return GlobalResponse.success("Portfolio deleted successfully");
  }

//  @PostMapping("/search-stocks")
//  public GlobalResponse<List<StockTempResponse>> searchStocks(
//      @RequestBody Map<String, String> body) {
//    String keyword = body.get("keyword");
//    List<Stock> stockList = pfStockService.searchStocks(keyword);
//    List<StockTempResponse> response =
//        stockList.stream().map(StockTempResponse::new).collect(Collectors.toList());
//    return GlobalResponse.success(response);
//  }
}
