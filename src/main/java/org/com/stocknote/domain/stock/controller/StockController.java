package org.com.stocknote.domain.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stock.dto.request.StockAddRequest;
import org.com.stocknote.domain.stock.dto.request.StockVoteRequest;
import org.com.stocknote.domain.stock.entity.VoteStatistics;
import org.com.stocknote.domain.stock.service.StockService;
import org.com.stocknote.domain.stock.service.StockVoteService;
import org.com.stocknote.domain.stockApi.dto.response.StockInfoResponse;
import org.com.stocknote.domain.stockApi.dto.response.StockResponse;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.com.stocknote.websocket.service.WebSocketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
@Tag(name = "관심종목 API", description = "관심 종목(Stock)")
public class StockController {

    private final StockService stockService;
    private final StockVoteService stockVoteService;
    private final WebSocketService webSocketService;

    @GetMapping
    @Operation(summary = "종목 이름 조회")
    public GlobalResponse findStock(@RequestParam String name) {
        StockInfoResponse stockInfoResponse = stockService.findStock(name);
        return GlobalResponse.success(stockInfoResponse);
    }

    @PostMapping
    @Operation(summary = "종목 추가")
    public GlobalResponse addStock(@RequestBody StockAddRequest request,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String email = principalDetails.getUsername();
        stockService.addStock(request.getStockName(), email);
        return GlobalResponse.success();
    }

    @GetMapping("/list")
    @Operation(summary = "나의 관심 종목 조회")
    public GlobalResponse getStockList(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        String email = principalDetails.getUsername();
        List<StockResponse> myStocks = stockService.getMyStocks(email);

        myStocks.forEach(stock -> {
            if (stock.getPrice() != null) {
                webSocketService.subscribeStockPrice(stock.getCode());
            }
        });
        return GlobalResponse.success(myStocks);
    }

    @PostMapping("/{stockCode}/vote")
    @Operation(summary = "종목 투표")
    public GlobalResponse<Void> vote(
            @PathVariable String stockCode,
            @RequestBody StockVoteRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String email = principalDetails.getUsername();
        stockVoteService.vote(stockCode, request, email);
        return GlobalResponse.success();
    }

    @GetMapping("/{stockCode}/vote-statistics")
    @Operation(summary = "종목 투표 통계 조회")
    public ResponseEntity<VoteStatistics> getVoteStatistics(
            @PathVariable String stockCode) {
        return ResponseEntity.ok(stockVoteService.getVoteStatistics(stockCode));
    }
}
