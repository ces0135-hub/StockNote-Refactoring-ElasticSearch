package org.com.stocknote.domain.portfolio.portfolioStock.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.request.PfStockPatchRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.request.PfStockRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.service.PfStockService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/portfolios/{portfolio_no}/stocks")
@Tag(name = "포트폴리오 주식 API", description = "포트폴리오 주식 API")
public class PfStockController {
  private final PfStockService pfStockService;

  @PostMapping("/AddStock")
  @Operation(summary = "포트폴리오 주식 추가")
  public GlobalResponse<String> addPortfolioStock(@PathVariable("portfolio_no") Long portfolioNo,
                                                  @RequestBody PfStockRequest pfStockRequest) {

    pfStockService.savePfStock(portfolioNo, pfStockRequest);
    return GlobalResponse.success("Portfolio post");
  }

  @PatchMapping("/{pfStock_no}/buyStock")
  @Operation(summary = "포트폴리오 주식 구매")
  public GlobalResponse<String> buyPortfolioStock(
      @PathVariable("portfolio_no") Long portfolioNo,
      @PathVariable("pfStock_no") Long pfStockNo,
      @RequestBody PfStockPatchRequest pfPfStockRequest) {

    pfStockService.buyPfStock(portfolioNo, pfStockNo, pfPfStockRequest);
    return GlobalResponse.success("buy Stock");
  }

  @PatchMapping("/{pfStock_no}/sellStock")
  @Operation(summary = "포트폴리오 주식 판매")
  public GlobalResponse<String> sellPortfolioStock(
      @PathVariable("portfolio_no") Long portfolioNo,
      @PathVariable("pfStock_no") Long pfStockNo,
      @RequestBody PfStockPatchRequest pfPfStockRequest) {

    pfStockService.sellPfStock(portfolioNo, pfStockNo, pfPfStockRequest);
    return GlobalResponse.success("sell Stock");
  }

  @PatchMapping("/{pfStock_no}")
  @Operation(summary = "포트폴리오 주식 수정")
  public GlobalResponse<String> patchPortfolioStock(
      @PathVariable("portfolio_no") Long portfolioNo,
      @PathVariable("pfStock_no") Long pfStockNo,
      @RequestBody PfStockPatchRequest pfPfStockRequest) {
    pfStockService.update(portfolioNo, pfStockNo, pfPfStockRequest);
    return GlobalResponse.success(" Portfolio modify");
  }

  @DeleteMapping("/{pfStock_no}")
  @Operation(summary = "포트폴리오 주식 삭제")
  public GlobalResponse<String> deletePortfolioStock(
      @PathVariable("portfolio_no") Long portfolioNo,
      @PathVariable("pfStock_no") Long pfStockNo) {

    pfStockService.deletePfStock(portfolioNo, pfStockNo);
    return GlobalResponse.success("Portfolio del");
  }
}
