package org.com.stocknote.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "댓글 알림 API", description = "댓글 알림 API")
public class CommentNotificationController {
    private final CommentNotificationService commentNotificationService;

    @GetMapping
    @Operation(summary = "댓글 알림 조회")
    public List<CommentNotificationResponse> getNotificationsByMember(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Long memberId = principalDetails.user().getId();
        return commentNotificationService.getNotificationsByMember(memberId);
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "댓글 알림 읽음 처리")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        commentNotificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
