package org.com.stocknote.domain.notification.dto;


import lombok.Builder;
import lombok.Getter;
import org.com.stocknote.domain.notification.entity.CommentNotification;
import org.com.stocknote.domain.notification.entity.KeywordNotification;
import org.com.stocknote.domain.post.entity.PostCategory;

import java.time.LocalDateTime;

@Getter
@Builder
public class KeywordNotificationResponse {
    private Long id;
    private String keyword;
    private String content;
    private Boolean isRead;
    private Long postId;
    private PostCategory postCategory;
    private LocalDateTime createdAt;

    public static KeywordNotificationResponse from(KeywordNotification keywordNotification) {
        return KeywordNotificationResponse.builder()
                .id(keywordNotification.getId())
                .keyword(keywordNotification.getKeyword())
                .content(keywordNotification.getContent())
                .isRead(keywordNotification.getIsRead())
                .postId(keywordNotification.getRelatedPostId())
                .postCategory(keywordNotification.getPostCategory())
                .createdAt(keywordNotification.getCreatedAt())
                .build();
    }
}

