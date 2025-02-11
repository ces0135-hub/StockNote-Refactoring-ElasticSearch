package org.com.stocknote.domain.notification.repository;

import org.com.stocknote.domain.notification.entity.CommentNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentNotificationRepository extends JpaRepository<CommentNotification, Long> {
    List<CommentNotification> findByMemberIdAndIsReadFalseAndCreatedAtAfterOrderByCreatedAtDesc(Long memberId, LocalDateTime startDate);

    void deleteByRelatedCommentId(Long commentId);
    void deleteByRelatedPostId(Long postId);
}
