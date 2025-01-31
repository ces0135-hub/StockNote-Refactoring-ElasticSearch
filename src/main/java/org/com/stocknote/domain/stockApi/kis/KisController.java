package org.com.stocknote.domain.stockApi.kis;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kis")
@Tag(name = "한국투자증권 API", description = "한국투자증권 관련 API")
public class KisController {
    private final KisService kisService;
    private final WebSocketClientService webSocketClientService;

    @GetMapping("/oauth/token")
    @Operation(summary = "Access Token 발급")
    public GlobalResponse getAccessToken() {
        String token = kisService.getAccessToken();
        return GlobalResponse.success(token);
    }

    @GetMapping("/websocket/approval-key")
    @Operation(summary = "WebSocket 접속 키 발급")
    public GlobalResponse getWebSocketApprovalKey() {
        String approvalKey = kisService.getWebSocketApprovalKey();
        return GlobalResponse.success(approvalKey);
    }


//    @GetMapping("/realtime/{stockCode}")
//    @Operation(summary = "실시간 주식 현재가 조회")
//    public GlobalResponse getRealtimePrice(@PathVariable String stockCode) {
//        String price = kisService.getRealtimePrice(stockCode);
//        return GlobalResponse.success(price);
//    }



}
