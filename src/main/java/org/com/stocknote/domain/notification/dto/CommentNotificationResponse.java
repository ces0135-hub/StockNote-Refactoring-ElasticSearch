package org.com.stocknote.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;
import org.com.stocknote.domain.notification.entity.CommentNotification;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentNotificationResponse {
    private Long id;
    private String content;
    private Boolean isRead;
    private Long postId;
    private LocalDateTime createdAt;

    public static CommentNotificationResponse from(CommentNotification commentNotification) {
        return CommentNotificationResponse.builder()
                .id(commentNotification.getId())
                .content(commentNotification.getContent())
                .isRead(commentNotification.getIsRead())
                .postId(commentNotification.getRelatedPostId())
                .createdAt(commentNotification.getCreatedAt())
                .build();
    }
}
