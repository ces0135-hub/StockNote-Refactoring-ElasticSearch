package org.com.stocknote.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.global.base.BaseEntity;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Getter
@Setter
public class CommentNotification extends BaseEntity {

    private Long memberId; // 알림 받는 사람

    private Long relatedPostId; // 관련 게시글

    private Long relatedCommentId; // 관련 댓글

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String content; // 알림 내용

    private Boolean isRead; // 읽음 여부

    public void markAsRead() {
        this.isRead = true;
    }
}