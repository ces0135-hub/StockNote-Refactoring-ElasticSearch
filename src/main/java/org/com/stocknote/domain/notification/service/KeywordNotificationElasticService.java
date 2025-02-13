package org.com.stocknote.domain.notification.service;

import lombok.RequiredArgsConstructor;

import org.com.stocknote.domain.keyword.repository.KeywordRepository;
import org.com.stocknote.domain.notification.dto.KeywordNotificationResponse;
import org.com.stocknote.domain.notification.entity.KeywordNotification;
import org.com.stocknote.domain.notification.repository.KeywordNotificationRepository;
import org.com.stocknote.domain.searchDoc.document.KeywordDoc;
import org.com.stocknote.domain.searchDoc.document.PostDoc;
import org.com.stocknote.domain.searchDoc.repository.KeywordDocRepository;
import org.com.stocknote.domain.searchDoc.repository.PostDocRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class KeywordNotificationElasticService {
    private final KeywordDocRepository keywordDocRepository;
    private final KeywordNotificationRepository keywordNotificationRepository;
    private final SseEmitterService sseEmitterService;

    public void createKeywordNotification(PostDoc postDoc) {

        List<KeywordDoc> matchingKeywords = keywordDocRepository.findMatchingKeywords(
                postDoc.getCategory(),
                postDoc.getTitle(),
                String.join(" ", postDoc.getHashtags())
        );

        System.out.println("matchingKeywords: " + matchingKeywords);

        matchingKeywords.forEach(keywordDoc -> {
            KeywordNotification notification = KeywordNotification.builder()
                    .memberId(keywordDoc.getMemberId())
                    .relatedPostId(Long.valueOf(postDoc.getId()))
                    .keyword(keywordDoc.getKeyword())
                    .postCategory(keywordDoc.getPostCategory())
                    .isRead(false)
                    .content(createNotificationContent(postDoc, keywordDoc.getKeyword()))
                    .build();

            keywordNotificationRepository.save(notification);

            // SSE로 실시간 알림 전송
            sseEmitterService.sendKeywordNotification(
                    keywordDoc.getMemberId().toString(),
                    KeywordNotificationResponse.from(notification)
            );
        });
    }

    private String createNotificationContent(PostDoc postDoc, String keyword) {
        return String.format("'%s' A post related to the keyword has been published : %s",
                keyword,
                postDoc.getTitle()
        );
    }

    // 키워드 알림 조회
    public List<KeywordNotificationResponse> getNotificationsByMember(Long memberId) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        return keywordNotificationRepository.findByMemberIdAndIsReadFalseAndCreatedAtAfterOrderByCreatedAtDesc(
                        memberId,
                        startDate
                )
                .stream()
                .map(KeywordNotificationResponse::from)
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리
    public void markAsRead(Long notificationId) {
        KeywordNotification keywordNotification = keywordNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
        keywordNotification.markAsRead();
    }
}
