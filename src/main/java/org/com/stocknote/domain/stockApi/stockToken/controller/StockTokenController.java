package org.com.stocknote.domain.stockApi.stockToken.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stockApi.stockToken.service.StockTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StockTokenController {
    private final StockTokenService stockTokenService;
    private String newAccessToken = "";

    @PostMapping("/api/token")
    public ResponseEntity<String> getToken() {
        try {
            String accessToken = stockTokenService.getAccessToken();

            return ResponseEntity.ok(accessToken);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


}
