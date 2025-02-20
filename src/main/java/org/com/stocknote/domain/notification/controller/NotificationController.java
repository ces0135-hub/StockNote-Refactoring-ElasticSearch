package org.com.stocknote.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.notification.dto.CommentNotificationResponse;
import org.com.stocknote.domain.notification.service.NotificationService;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/user")
    public List<CommentNotificationResponse> getNotificationsByMember(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long memberId = principalDetails.user().getId();
        return notificationService.getNotificationsByMember(memberId);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}