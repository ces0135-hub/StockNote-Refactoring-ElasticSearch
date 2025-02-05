package org.com.stocknote.domain.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stock.dto.request.StockAddRequest;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stock.service.StockService;
import org.com.stocknote.domain.stockVote.service.StockVoteService;
import org.com.stocknote.domain.stockApi.dto.response.StockInfoResponse;
import org.com.stocknote.domain.stockApi.dto.response.StockResponse;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.com.stocknote.websocket.service.WebSocketService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    public GlobalResponse findStock(@RequestParam("name") String name) {
        StockInfoResponse stockInfoResponse = stockService.findStock(name);
        return GlobalResponse.success(stockInfoResponse);
    }

    @PostMapping("/search-stocks")
    @Operation(summary = "종목 검색")
    public GlobalResponse<List<StockInfoResponse>> searchStocks(
        @RequestBody Map<String, String> body) {
        String keyword = body.get("keyword");
        List<Stock> stockList = stockService.searchStocks(keyword);
        List<StockInfoResponse> response =
            stockList.stream().map(StockInfoResponse::of).collect(Collectors.toList());
        return GlobalResponse.success(response);
    }

    @PostMapping
    @Operation(summary = "종목 추가")
    public GlobalResponse addStock(@RequestBody StockAddRequest request,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String email = principalDetails.getUsername();
        stockService.addStock(request.getStockName(), email);
        return GlobalResponse.success();
    }

    @DeleteMapping
    @Operation(summary = "종목 삭제")
    public GlobalResponse deleteStock(@RequestParam("stockCode") String stockCode,
                                      @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String email = principalDetails.getUsername();
        stockService.deleteStock(stockCode, email);
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
}
