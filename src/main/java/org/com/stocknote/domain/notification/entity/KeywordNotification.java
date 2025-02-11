package org.com.stocknote.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.global.base.BaseEntity;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Getter
@Setter
public class KeywordNotification extends BaseEntity {

    private Long memberId; // 알림 받는 사람

    private String keyword; // 키워드

    private Long relatedPostId; // 관련 게시글

    private PostCategory postCategory; // 게시글 카테고리

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String content; // 알림 내용

    private Boolean isRead; // 읽음 여부

    public void markAsRead() {
        this.isRead = true;
    }
}