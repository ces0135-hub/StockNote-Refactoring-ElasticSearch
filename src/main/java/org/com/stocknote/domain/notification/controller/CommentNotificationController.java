package org.com.stocknote.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.notification.dto.CommentNotificationResponse;
import org.com.stocknote.domain.notification.service.CommentNotificationService;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications/comment")
@RequiredArgsConstructor
public class CommentNotificationController {
    private final CommentNotificationService commentNotificationService;

    @GetMapping
    public List<CommentNotificationResponse> getNotificationsByMember(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long memberId = principalDetails.user().getId();
        return commentNotificationService.getNotificationsByMember(memberId);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        commentNotificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}