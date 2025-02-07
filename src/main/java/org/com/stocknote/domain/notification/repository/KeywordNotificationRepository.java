package org.com.stocknote.domain.notification.repository;

import org.com.stocknote.domain.notification.entity.KeywordNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;

public interface KeywordNotificationRepository extends JpaRepository<KeywordNotification, Long> {
    Collection<KeywordNotification> findByMemberIdAndIsReadFalseAndCreatedAtAfterOrderByCreatedAtDesc(Long memberId, LocalDateTime startDate);

    void deleteByRelatedPostId(Long id);
}
