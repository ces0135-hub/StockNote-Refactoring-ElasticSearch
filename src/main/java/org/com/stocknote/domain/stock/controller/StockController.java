package org.com.stocknote.domain.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stock.entity.PeriodType;
import org.com.stocknote.domain.stock.dto.request.StockVoteRequest;
import org.com.stocknote.domain.stock.dto.response.*;
import org.com.stocknote.domain.stock.entity.VoteStatistics;
import org.com.stocknote.domain.stock.service.StockChartService;
import org.com.stocknote.domain.stock.service.StockDataService;
import org.com.stocknote.domain.stock.service.StockService;
import org.com.stocknote.domain.stock.service.StockVoteService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
@Tag(name = "관심종목 API", description = "관심 종목(Stock)")
public class StockController {
    private final StockService stockService;
    private final StockChartService stockChartService;
    private final StockVoteService stockVoteService;
    private final StockDataService stockDataService;

    //이름으로 종목 검색
    @GetMapping
    @Operation(summary = "종목 이름 검색")
    public GlobalResponse findStock(@RequestParam String name) {
        StockInfoResponse stockInfoResponse = stockDataService.findStock(name);
        return GlobalResponse.success(stockInfoResponse);
    }

    @PostMapping
    @Operation(summary = "종목 추가")
    public GlobalResponse addStock(@RequestParam String stockCode,
                                   Authentication authentication) {
        String email = authentication.getName();
        stockService.addStock(stockCode, email);
        return GlobalResponse.success();
    }

    @GetMapping("/price")
    public StockPriceResponse getStockPrice(@RequestParam String stockCode) {
        return stockService.getStockPrice(stockCode);
    }

    @GetMapping("/daily-prices")
    @Operation(summary = "일별 주식 데이터 조회")
    public StockDailyResponse getDailyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockService.getDailyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/weekly-prices")
    @Operation(summary = "주별 주식 데이터 조회")
    public StockDailyResponse getWeeklyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockService.getWeeklyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/monthly-prices")
    @Operation(summary = "월별 주식 데이터 조회")
    public StockDailyResponse getMonthlyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockService.getMonthlyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/yearly-prices")
    @Operation(summary = "연간 주식 데이터 조회")
    public StockDailyResponse getYearlyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockService.getYearlyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/time-prices")
    @Operation(summary = "당일 시간대별 체결 정보 조회")
    public StockTimeResponse getTimeStockPrices(@RequestParam String stockCode) {
        return stockService.getTimeStockPrices(stockCode);
    }

    @GetMapping("/chart")
    public ChartResponse getChartData(
            @RequestParam String stockCode,
            @RequestParam PeriodType periodType,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockChartService.getChartData(stockCode, periodType, startDate, endDate);
    }

    //추후 유저 추가
    @PostMapping("/{stockCode}/vote")
    public GlobalResponse<Void> vote(
            @PathVariable String stockCode,
            @RequestBody StockVoteRequest request) {
        stockVoteService.vote(stockCode, request.getVoteType());
        return GlobalResponse.success();
    }
    // 투표기능
    @GetMapping("/{stockCode}/vote-statistics")
    public ResponseEntity<VoteStatistics> getVoteStatistics(@PathVariable String stockCode) {
        return ResponseEntity.ok(stockVoteService.getVoteStatistics(stockCode));
    }
}
