package org.com.stocknote.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.keyword.entity.Keyword;
import org.com.stocknote.domain.keyword.repository.KeywordRepository;
import org.com.stocknote.domain.notification.dto.KeywordNotificationResponse;
import org.com.stocknote.domain.notification.entity.KeywordNotification;
import org.com.stocknote.domain.notification.repository.KeywordNotificationRepository;
import org.com.stocknote.domain.post.entity.PostCategory;
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
@RequiredArgsConstructor
public class KeywordNotificationElasticService {
    private final KeywordRepository keywordRepository;
    private final KeywordDocRepository keywordDocRepository;
    private final KeywordNotificationRepository keywordNotificationRepository;
    private final SseEmitterService sseEmitterService;
    private final PostDocRepository postDocRepository;

    @Transactional
    public void createKeywordNotification(PostDoc postDoc) {
        // Elasticsearch로 매칭되는 키워드 한 번에 조회
        System.out.println("postDoc = " + postDoc);
        System.out.println("postCategory = " + postDoc.getCategory());
        System.out.println("postDoc = " + postDoc.getHashtags());

        List<KeywordDoc> matchingKeywords = keywordDocRepository.findMatchingKeywords(
                postDoc.getCategory(),
                postDoc.getTitle(),
                String.join(" ", postDoc.getHashtags())
        );

        System.out.println("matchingKeywords = " + matchingKeywords);

        // 매칭된 키워드에 대해 알림 생성
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
        return String.format("'%s' 키워드와 관련된 게시글이 등록되었습니다: %s",
                keyword,
                postDoc.getTitle()
        );
    }

}
