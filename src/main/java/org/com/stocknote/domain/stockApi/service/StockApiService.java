package org.com.stocknote.domain.stockApi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.stockApi.dto.*;
import org.com.stocknote.domain.stock.type.PeriodType;
import org.com.stocknote.domain.stockApi.dto.response.*;
import org.com.stocknote.domain.stockApi.stockToken.service.StockTokenService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockApiService {

    private final WebClient.Builder webClientBuilder;
    private final StockTokenService stockTokenService;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");


    public Mono<CurrentIndexResponse> getKOSPI() {
        String accessToken = stockTokenService.getAccessToken();
        WebClient webClient = webClientBuilder.baseUrl(stockTokenService.getIndexBaseUrl()).build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                        .queryParam("FID_INPUT_ISCD", "0001")
                        .build())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("tr_id", "FHPUP02100000")
                .header("custtype", "P")
                .header("appkey", stockTokenService.getAppKey())
                .header("appsecret", stockTokenService.getAppSecret())
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(CurrentIndexResponse.class);
    }

    public Mono<CurrentIndexResponse> getKOSDAQ() {
        String accessToken = stockTokenService.getAccessToken();
        WebClient webClient = webClientBuilder.baseUrl(stockTokenService.getIndexBaseUrl()).build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                        .queryParam("FID_INPUT_ISCD", "1001")
                        .build())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("tr_id", "FHPUP02100000")
                .header("custtype", "P")
                .header("appkey", stockTokenService.getAppKey())
                .header("appsecret", stockTokenService.getAppSecret())
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(CurrentIndexResponse.class);
    }

    public Mono<CurrentIndexResponse> getKOSPI200() {
        String accessToken = stockTokenService.getAccessToken();
        WebClient webClient = webClientBuilder.baseUrl(stockTokenService.getIndexBaseUrl()).build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "U")
                        .queryParam("FID_INPUT_ISCD", "2001")
                        .build())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("tr_id", "FHPUP02100000")
                .header("custtype", "P")
                .header("appkey", stockTokenService.getAppKey())
                .header("appsecret", stockTokenService.getAppSecret())
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(CurrentIndexResponse.class);
    }

    public Mono<VolumeResponse> getVolumeData() {
        String accessToken = stockTokenService.getAccessToken();
        WebClient webClient = webClientBuilder.baseUrl(stockTokenService.getVolumeBaseUrl()).build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/volume-rank")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_COND_SCR_DIV_CODE", "20171")
                        .queryParam("FID_INPUT_ISCD", "0000")
                        .queryParam("FID_DIV_CLS_CODE", "0")
                        .queryParam("FID_BLNG_CLS_CODE", "0")
                        .queryParam("FID_TRGT_CLS_CODE", "111111111")
                        .queryParam("FID_TRGT_EXLS_CLS_CODE", "0000000000")
                        .queryParam("FID_INPUT_PRICE_1", "")
                        .queryParam("FID_INPUT_PRICE_2", "")
                        .queryParam("FID_VOL_CNT", "")
                        .queryParam("FID_INPUT_DATE_1", "")
                        .build())
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("tr_id", "FHPST01710000")
                .header("appkey", stockTokenService.getAppKey())
                .header("appsecret", stockTokenService.getAppSecret())
                .header("authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(VolumeResponse.class);
    }


    // 필터링한 정보
    public Mono<StockIndexDto> getFilteredKOSPI() {
        return getKOSPI().map(this::KOSPIIntoStockIndexDto);
    }

    public Mono<StockIndexDto> getFilteredKOSDAQ() {
        return getKOSDAQ().map(this::KOSDAQIntoStockIndexDto);
    }

    public Mono<StockIndexDto> getFilteredKOSPI200() {
        return getKOSPI200().map(this::KOSPI200IntoStockIndexDto);
    }


    private StockIndexDto KOSPIIntoStockIndexDto(CurrentIndexResponse responseDto) {
        StockIndexDto dto = new StockIndexDto();
        CurrentIndexResponse.CurrentIndexData data = responseDto.getOutput();

        dto.setIndexName("코스피");
        dto.setCurrentValue(data.getBstp_nmix_prpr());  // 현재가
        dto.setChangeAmount(data.getBstp_nmix_prdy_vrss());  // 변화량
        dto.setChangeRate(data.getBstp_nmix_prdy_ctrt()); // 변화율
        dto.setChangeDirection(data.getPrdy_vrss_sign().equals("1") ? "▲" : "▼");  // 상승/하락 표시

        return dto;
    }

    private StockIndexDto KOSDAQIntoStockIndexDto(CurrentIndexResponse responseDto) {
        StockIndexDto dto = new StockIndexDto();
        CurrentIndexResponse.CurrentIndexData data = responseDto.getOutput();

        dto.setIndexName("코스닥");
        dto.setCurrentValue(data.getBstp_nmix_prpr());  // 현재가
        dto.setChangeAmount(data.getBstp_nmix_prdy_vrss());  // 변화량
        dto.setChangeRate(data.getBstp_nmix_prdy_ctrt()); // 변화율
        dto.setChangeDirection(data.getPrdy_vrss_sign().equals("1") ? "▲" : "▼");  // 상승/하락 표시

        return dto;
    }

    private StockIndexDto KOSPI200IntoStockIndexDto(CurrentIndexResponse responseDto) {
        StockIndexDto dto = new StockIndexDto();
        CurrentIndexResponse.CurrentIndexData data = responseDto.getOutput();

        dto.setIndexName("코스피200");
        dto.setCurrentValue(data.getBstp_nmix_prpr());  // 현재가
        dto.setChangeAmount(data.getBstp_nmix_prdy_vrss());  // 변화량
        dto.setChangeRate(data.getBstp_nmix_prdy_ctrt());  // 변화율
        dto.setChangeDirection(data.getPrdy_vrss_sign().equals("1") ? "▲" : "▼");  // 상승/하락 표시

        return dto;
    }

    //실시간 가격 조회
    public Mono<StockPriceResponse> getStockPrice(String stockCode) {
        return webClientBuilder
                .baseUrl(stockTokenService.getIndexBaseUrl())
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-price")
                        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                        .queryParam("FID_INPUT_ISCD", stockCode)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set("authorization", "Bearer " + stockTokenService.getAccessToken());
                    headers.set("appkey", stockTokenService.getAppKey());
                    headers.set("appsecret", stockTokenService.getAppSecret());
                    headers.set("tr_id", "FHKST01010100");
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new RuntimeException("주식 가격 조회 실패: " + response.statusCode()))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException("서버 오류: " + response.statusCode()))
                )
                .bodyToMono(StockPriceResponse.class)
                .onErrorMap(WebClientResponseException.class, e ->
                        new RuntimeException("주식 가격 조회 실패: " + e.getStatusCode(), e)
                )
                .onErrorMap(Exception.class, e ->
                        new RuntimeException("주식 가격 조회 중 오류 발생", e)
                );
    }

    // 날자별 가격 조회
    public StockDailyResponse getStockPrices(String stockCode, PeriodType periodType, LocalDate startDate, LocalDate endDate) {
        String baseUrl = "https://openapivts.koreainvestment.com:29443";
        String endpoint = "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice";

        // 날짜 포맷 변환
        String formattedStartDate = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        try {
            String response = webClientBuilder
                    .baseUrl(baseUrl)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(endpoint)
                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                            .queryParam("FID_INPUT_ISCD", stockCode)
                            .queryParam("FID_INPUT_DATE_1", formattedStartDate)
                            .queryParam("FID_INPUT_DATE_2", formattedEndDate)
                            .queryParam("FID_PERIOD_DIV_CODE", periodType.getCode())
                            .queryParam("FID_ORG_ADJ_PRC", "0")
                            .build())
                    .headers(headers -> {
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("authorization", "Bearer " + stockTokenService.getAccessToken());
                        headers.set("appkey", stockTokenService.getAppKey());
                        headers.set("appsecret", stockTokenService.getAppSecret());
                        headers.set("tr_id", "FHKST03010100");
                    })
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        return Mono.error(new RuntimeException("주식 데이터 조회 실패: " + errorBody));
                                    })
                    )
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(response, StockDailyResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("주식 데이터 조회 중 오류 발생", e);
        }
    }

    // 일별 데이터 조회
    public StockDailyResponse getDailyStockPrices(String stockCode, LocalDate startDate, LocalDate endDate) {
        return getStockPrices(stockCode, PeriodType.DAILY, startDate, endDate);
    }

    // 주별 데이터 조회
    public StockDailyResponse getWeeklyStockPrices(String stockCode, LocalDate startDate, LocalDate endDate) {
        return getStockPrices(stockCode, PeriodType.WEEKLY, startDate, endDate);
    }

    // 월별 데이터 조회
    public StockDailyResponse getMonthlyStockPrices(String stockCode, LocalDate startDate, LocalDate endDate) {
        return getStockPrices(stockCode, PeriodType.MONTHLY, startDate, endDate);
    }

    // 연간 데이터 조회
    public StockDailyResponse getYearlyStockPrices(String stockCode, LocalDate startDate, LocalDate endDate) {
        return getStockPrices(stockCode, PeriodType.YEARLY, startDate, endDate);
    }

    public StockTimeResponse getTimeStockPrices(String stockCode) {
        String baseUrl = "https://openapivts.koreainvestment.com:29443";
        String endpoint = "/uapi/domestic-stock/v1/quotations/inquire-time-itemconclusion";

        String formattedTime = LocalTime.now().format(TIME_FORMATTER);

        try {
            String response = webClientBuilder
                    .baseUrl(baseUrl)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(endpoint)
                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                            .queryParam("FID_INPUT_ISCD", stockCode)
                            .queryParam("FID_INPUT_HOUR_1", formattedTime)
                            .build())
                    .headers(headers -> {
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("authorization", "Bearer " + stockTokenService.getAccessToken());
                        headers.set("appkey", stockTokenService.getAppKey());
                        headers.set("appsecret", stockTokenService.getAppSecret());
                        headers.set("tr_id", "FHPST01060000");
                    })
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Client error for stockCode {}: {}", stockCode, errorBody);
                                        return Mono.error(new RuntimeException("시간대별 주식 데이터 조회 실패: " + errorBody));
                                    })
                    )
                    .bodyToMono(String.class)
                    .block();

            log.debug("API Response: {}", response);

            StockTimeResponse timeResponse = objectMapper.readValue(response, StockTimeResponse.class);

            if (timeResponse == null || timeResponse.getOutput2() == null || timeResponse.getOutput2().isEmpty()) {
                log.warn("No time data returned for stockCode: {}", stockCode);
            }

            return timeResponse;

        } catch (JsonProcessingException e) {
            log.error("JSON parsing error for stockCode {}: {}", stockCode, e.getMessage());
            throw new RuntimeException("시간대별 주식 데이터 파싱 실패", e);
        } catch (Exception e) {
            log.error("API Error for stockCode {}: {}", stockCode, e.getMessage());
            throw new RuntimeException("시간대별 주식 데이터 조회 중 오류 발생", e);
        }
    }



}
