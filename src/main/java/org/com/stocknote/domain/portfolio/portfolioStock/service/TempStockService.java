package org.com.stocknote.domain.portfolio.portfolioStock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.stock.dto.response.StockPriceResponse;
import org.com.stocknote.domain.stock.token.service.StockTokenService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class TempStockService {
  private final StockTokenService stockTokenService;
  private final RestTemplate restTemplate;

  public StockPriceResponse getStockPrice(String stockCode) {
    String baseUrl = "https://openapivts.koreainvestment.com:29443";  // Change to production URL if needed
    String endpoint = "/uapi/domestic-stock/v1/quotations/inquire-price";

    // Build URL with query parameters
    String url = UriComponentsBuilder.fromHttpUrl(baseUrl + endpoint)
        .queryParam("FID_COND_MRKT_DIV_CODE", "J")
        .queryParam("FID_INPUT_ISCD", stockCode)
        .toUriString();

    // Set up headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("authorization", "Bearer " + stockTokenService.getAccessToken());
    headers.set("appkey", stockTokenService.getAppKey());
    headers.set("appsecret", stockTokenService.getAppSecret());
    headers.set("tr_id", "FHKST01010100");

    HttpEntity<?> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<StockPriceResponse> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          entity,
          StockPriceResponse.class
      );

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return response.getBody();
      } else {
        throw new RuntimeException("Failed to get stock price. Status code: " + response.getStatusCode());
      }
    } catch (HttpStatusCodeException e) {
      throw new RuntimeException("주식 가격 조회 실패: " + e.getResponseBodyAsString(), e);
    } catch (Exception e) {
      throw new RuntimeException("주식 가격 조회 중 오류 발생", e);
    }
  }
}
