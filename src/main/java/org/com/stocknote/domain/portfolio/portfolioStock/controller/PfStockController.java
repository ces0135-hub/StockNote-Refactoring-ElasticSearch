package org.com.stocknote.domain.portfolio.portfolioStock.controller;


import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.request.PfStockPatchRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.request.PfStockRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.service.PfStockService;
import org.com.stocknote.global.dto.GlobalResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios/{portfolio_no}/stocks")
public class PfStockController {
  private final PfStockService pfStockService;



  @PostMapping("/AddStock")
  public GlobalResponse<String> addPortfolioStock(@PathVariable("portfolio_no") Long portfolioNo,
      @RequestBody PfStockRequest pfStockRequest) {

    pfStockService.savePfStock(portfolioNo, pfStockRequest);
    return GlobalResponse.success("Portfolio post");
  }

  @PatchMapping("/{pfStock_no}/buyStock")
  public GlobalResponse<String> buyPortfolioStock(
      @PathVariable("portfolio_no") Long portfolioNo,
      @PathVariable("pfStock_no") Long pfStockNo,
      @RequestBody PfStockPatchRequest pfPfStockRequest) {

    pfStockService.buyPfStock(portfolioNo, pfStockNo, pfPfStockRequest);
    return GlobalResponse.success("buy Stock");
  }

  @PatchMapping("/{pfStock_no}/sellStock")
  public GlobalResponse<String> sellPortfolioStock(
      @PathVariable("portfolio_no") Long portfolioNo,
      @PathVariable("pfStock_no") Long pfStockNo,
      @RequestBody PfStockPatchRequest pfPfStockRequest) {

    pfStockService.sellPfStock(portfolioNo, pfStockNo, pfPfStockRequest);
    return GlobalResponse.success("sell Stock");
  }

  @PatchMapping("/{pfStock_no}")
  public GlobalResponse<String> patchPortfolioStock(@PathVariable("pfStock_no") Long pfStockNo,
      @RequestBody PfStockPatchRequest pfPfStockRequest) {
    pfStockService.update(pfStockNo, pfPfStockRequest);
    return GlobalResponse.success(" Portfolio modify");
  }

  @DeleteMapping("/{pfStock_no}")
  public GlobalResponse<String> deletePortfolioStock(@PathVariable("portfolio_no") Long portfolioNo,
      @PathVariable("pfStock_no") Long pfStockNo) {

    pfStockService.deletePfStock(pfStockNo);
    return GlobalResponse.success("Portfolio del");
  }


}
