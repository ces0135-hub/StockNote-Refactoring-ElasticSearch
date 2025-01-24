package org.com.stocknote.domain.stock.token.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stock.token.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;
    private String newAccessToken = "";

    @PostMapping("/api/token")
    public ResponseEntity<String> getToken() {
        try {
            String accessToken = tokenService.getAccessToken();

            return ResponseEntity.ok(accessToken);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


}