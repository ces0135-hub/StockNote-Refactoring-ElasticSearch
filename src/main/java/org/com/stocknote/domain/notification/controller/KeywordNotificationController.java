package org.com.stocknote.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.notification.dto.KeywordNotificationResponse;
import org.com.stocknote.domain.notification.service.KeywordNotificationElasticService;
import org.com.stocknote.domain.notification.service.KeywordNotificationService;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications/keyword")
@RequiredArgsConstructor
@Tag(name = "키워드 알림 API", description = "키워드 알림 API")
public class KeywordNotificationController {
    private final KeywordNotificationService keywordNotificationService;

    @GetMapping
    @Operation(summary = "키워드 알림 조회")
    public List<KeywordNotificationResponse> getNotificationsByMember(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long memberId = principalDetails.user().getId();
        return keywordNotificationService.getNotificationsByMember(memberId);
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "키워드 알림 읽음 처리")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        keywordNotificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
