package org.com.stocknote.domain.stockApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stockApi.dto.*;
import org.com.stocknote.domain.stockApi.dto.response.*;
import org.com.stocknote.domain.stockApi.service.StockApiService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stockApis")
public class StockApiController {
    private final StockApiService stockApiService;

    // 전체 정보 불러오기
    @GetMapping("/kospi")
    public Mono<CurrentIndexResponse> getKospiData() {
        return stockApiService.getKOSPI();
    }

    @GetMapping("/kosdaq")
    public Mono<CurrentIndexResponse> getKosdaqData() {
        return stockApiService.getKOSDAQ();
    }

    @GetMapping("/kospi200")
    public Mono<CurrentIndexResponse> getKospi200Data() {
        return stockApiService.getKOSPI200();
    }

    // 필터링한 정보 불러오기
    @GetMapping("/filtered/kospi")
    public Mono<StockIndexDto> getFilteredKOSPIData() {
        return stockApiService.getFilteredKOSPI();
    }

    @GetMapping("/filtered/kosdaq")
    public Mono<StockIndexDto> getFilteredKOSDAQData() {
        return stockApiService.getFilteredKOSDAQ();
    }

    @GetMapping("/filtered/kospi200")
    public Mono<StockIndexDto> getFilteredKOSPI200Data() {
        return stockApiService.getFilteredKOSPI200();
    }

    @GetMapping("/api/volume")
    public Mono<VolumeResponse> getVolume() {
        return stockApiService.getVolumeData();
    }

    @GetMapping("/price")
    @Operation(summary = "현재 가격 조회")
    public Mono<StockPriceResponse> getStockPrice(@RequestParam String stockCode) {
        return stockApiService.getStockPrice(stockCode);
    }

    @GetMapping("/daily-prices")
    @Operation(summary = "일별 주식 데이터 조회")
    public StockDailyResponse getDailyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockApiService.getDailyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/weekly-prices")
    @Operation(summary = "주별 주식 데이터 조회")
    public StockDailyResponse getWeeklyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockApiService.getWeeklyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/monthly-prices")
    @Operation(summary = "월별 주식 데이터 조회")
    public StockDailyResponse getMonthlyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockApiService.getMonthlyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/yearly-prices")
    @Operation(summary = "연간 주식 데이터 조회")
    public StockDailyResponse getYearlyStockPrices(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return stockApiService.getYearlyStockPrices(stockCode, startDate, endDate);
    }

    @GetMapping("/time-prices")
    @Operation(summary = "당일 시간대별 체결 정보 조회")
    public StockTimeResponse getTimeStockPrices(@RequestParam String stockCode) {
        return stockApiService.getTimeStockPrices(stockCode);
    }

//    @GetMapping("/chart")
//    public ChartResponse getChartData(
//            @RequestParam String stockCode,
//            @RequestParam PeriodType periodType,
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
//        return stockService.getChartData(stockCode, periodType, startDate, endDate);
//    }


}
