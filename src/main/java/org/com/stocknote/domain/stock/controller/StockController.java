package org.com.stocknote.domain.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.post.service.PostService;
import org.com.stocknote.domain.stock.dto.request.StockAddRequest;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stock.service.StockService;
import org.com.stocknote.domain.stockApi.dto.response.StockInfoResponse;
import org.com.stocknote.domain.stockApi.dto.response.StockResponse;
import org.com.stocknote.global.aop.InjectEmail;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.com.stocknote.websocket.service.WebSocketService;
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
    private final WebSocketService webSocketService;
    private final PostService postService;

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
    @InjectEmail
    public GlobalResponse addStock(@RequestBody StockAddRequest request) {
        stockService.addStock(request.getStockName());
        return GlobalResponse.success();
    }

    @DeleteMapping
    @Operation(summary = "종목 삭제")
    @InjectEmail
    public GlobalResponse deleteStock(@RequestParam("stockCode") String stockCode) {
        stockService.deleteStock(stockCode);
        return GlobalResponse.success();
    }

    @GetMapping("/list")
    @Operation(summary = "나의 관심 종목 조회")
    @InjectEmail
    public GlobalResponse getStockList() {
        List<StockResponse> myStocks = stockService.getMyStocks();

        myStocks.forEach(stock -> {
            if (stock.getPrice() != null) {
                webSocketService.subscribeStockPrice(stock.getCode());
            }
        });
        return GlobalResponse.success(myStocks);
    }

    @GetMapping("/posts")
    @Operation(summary = "종목 관련 게시글 조회")
    public GlobalResponse getPosts(@RequestParam("sName") String sName,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "3") int size) {
        return GlobalResponse.success(postService.getPostsByStockName(sName, page, size));
    }
}
