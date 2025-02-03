package org.com.stocknote.domain.portfolio.portfolioStock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.response.SectorTempResponse;
import org.com.stocknote.domain.stockApi.stockToken.service.StockTokenService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class TempStockInfoService {
  private final StockTokenService stockTokenService;
  private final RestTemplate restTemplate;

  public SectorTempResponse getStockInfo(String pdno) {
    log.info("Getting stock info for PDNO: {}", pdno);
    String baseUrl = "https://openapi.koreainvestment.com:9443";
    String endpoint = "/uapi/domestic-stock/v1/quotations/search-stock-info";

    // Build URL with query parameters
    String url = UriComponentsBuilder.fromHttpUrl(baseUrl + endpoint)
        .queryParam("PDNO", pdno)
        .queryParam("PRDT_TYPE_CD", "300")
        .toUriString();

    // Set up headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("authorization", "Bearer " + stockTokenService.getAccessToken());
    headers.set("appkey", stockTokenService.getAppKey());
    headers.set("appsecret", stockTokenService.getAppSecret());
    headers.set("tr_id", "CTPF1002R");
    headers.set("custtype", "P");

    HttpEntity<?> entity = new HttpEntity<>(headers);

    try {
      log.info("Requesting stock info for PDNO: {}", pdno);
      ResponseEntity<SectorTempResponse> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          entity,
          SectorTempResponse.class
      );

      if (response.getStatusCode() == HttpStatus.OK) {
        log.debug("Successfully retrieved stock info for PDNO: {}", pdno);
        log.debug("Stock info: {}", response.getBody().toString());
        return response.getBody();
      } else {
        log.error("Failed to get stock info. Status code: {}", response.getStatusCode());
        throw new RuntimeException("Failed to get stock info. Status code: " + response.getStatusCode());
      }
    } catch (HttpStatusCodeException e) {
      log.error("Stock info lookup failed: {}", e.getResponseBodyAsString(), e);
      throw new RuntimeException("주식 정보 조회 실패: " + e.getResponseBodyAsString(), e);
    } catch (Exception e) {
      log.error("Error occurred while getting stock info", e);
      throw new RuntimeException("주식 정보 조회 중 오류 발생", e);
    }
  }
}
