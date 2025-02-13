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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
