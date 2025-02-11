package org.com.stocknote.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.hashtag.service.HashtagService;
import org.com.stocknote.domain.keyword.entity.Keyword;
import org.com.stocknote.domain.keyword.repository.KeywordRepository;
import org.com.stocknote.domain.notification.dto.KeywordNotificationResponse;
import org.com.stocknote.domain.notification.entity.KeywordNotification;
import org.com.stocknote.domain.notification.repository.KeywordNotificationRepository;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.searchDoc.document.PostDoc;
import org.com.stocknote.domain.searchDoc.repository.PostDocRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordNotificationElasticService {
    private final KeywordRepository keywordRepository;
    private final KeywordNotificationRepository keywordNotificationRepository;
    private final SseEmitterService sseEmitterService;
    private final PostDocRepository postDocRepository;

    @Transactional
    public void createKeywordNotification(PostDoc postDoc) {
        // 카테고리에 맞는 키워드 구독자 조회
        List<Keyword> keywords;
        if (postDoc.getCategory() == PostCategory.ALL) {
            keywords = keywordRepository.findAll();
        } else {
            keywords = keywordRepository.findAllByPostCategory(postDoc.getCategory());
        }

        // 각 키워드별로 매칭 확인
        keywords.forEach(keyword -> {
            if (postDocRepository.existsByTitleOrHashtagsContaining(keyword.getKeyword())) {
                KeywordNotification keywordNotification = KeywordNotification.builder()
                    .memberId(keyword.getMemberId())
                    .relatedPostId(Long.valueOf(postDoc.getId()))
                    .keyword(keyword.getKeyword())
                    .postCategory(keyword.getPostCategory())
                    .isRead(false)
                    .content(createNotificationContent(postDoc, keyword.getKeyword()))
                    .build();

                keywordNotificationRepository.save(keywordNotification);

                // SSE로 실시간 알림 전송
                sseEmitterService.sendKeywordNotification(
                    keyword.getMemberId().toString(),
                    KeywordNotificationResponse.from(keywordNotification)
                );
            }
        });
    }

    private String createNotificationContent(PostDoc postDoc, String keyword) {
        return String.format("'%s' 키워드와 관련된 게시글이 등록되었습니다: %s",
            keyword,
            postDoc.getTitle()
        );
    }

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

    public void markAsRead(Long notificationId) {
        KeywordNotification keywordNotification = keywordNotificationRepository.findById(notificationId)
            .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
        keywordNotification.markAsRead();
    }
}
