package org.com.stocknote.domain.notification.controller;

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
public class KeywordNotificationController {
    private final KeywordNotificationService keywordNotificationService;

    @GetMapping
    public List<KeywordNotificationResponse> getNotificationsByMember(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long memberId = principalDetails.user().getId();
        return keywordNotificationService.getNotificationsByMember(memberId);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        keywordNotificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
