package org.com.stocknote.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;
import org.com.stocknote.domain.notification.entity.Notification;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String content;
    private Boolean isRead;
    private Long postId;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .isRead(notification.getIsRead())
                .postId(notification.getRelatedPostId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
