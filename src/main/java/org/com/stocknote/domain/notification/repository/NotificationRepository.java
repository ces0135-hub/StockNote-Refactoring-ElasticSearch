package org.com.stocknote.domain.notification.repository;

import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberIdAndIsReadFalseAndCreatedAtAfterOrderByCreatedAtDesc(Member member, LocalDateTime startDate);

    void deleteByRelatedCommentId(Long commentId);
    void deleteByRelatedPostId(Long postId);
}
