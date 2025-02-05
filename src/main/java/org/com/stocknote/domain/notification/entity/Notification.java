package org.com.stocknote.domain.notification.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.comment.entity.Comment;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.global.base.BaseEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Getter
@Setter
public class Notification  extends BaseEntity {

    private Long memberId; // 알림 받는 사람

    private Long relatedPostId; // 관련 게시글

    private Long relatedCommentId; // 관련 댓글

    private String content; // 알림 내용

    private Boolean isRead; // 읽음 여부

    public void markAsRead() {
        this.isRead = true;
    }
}