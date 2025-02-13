//package org.com.stocknote.domain.notification.service;
//
//import lombok.RequiredArgsConstructor;
//import org.com.stocknote.domain.hashtag.service.HashtagService;
//import org.com.stocknote.domain.keyword.entity.Keyword;
//import org.com.stocknote.domain.keyword.repository.KeywordRepository;
//import org.com.stocknote.domain.notification.dto.KeywordNotificationResponse;
//import org.com.stocknote.domain.notification.entity.KeywordNotification;
//import org.com.stocknote.domain.notification.repository.KeywordNotificationRepository;
//import org.com.stocknote.domain.post.entity.Post;
//import org.com.stocknote.domain.post.entity.PostCategory;
//import org.com.stocknote.global.error.ErrorCode;
//import org.com.stocknote.global.exception.CustomException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class KeywordNotificationService {
//    private final KeywordRepository keywordRepository;
//    private final KeywordNotificationRepository keywordNotificationRepository;
//    private final HashtagService hashtagService;
//    private final SseEmitterService sseEmitterService;
//
//    public void createKeywordNotification(Post post) {
//        // 변수를 블록 외부에서 선언
//        List<Keyword> allKeywords;
//
//        // 게시글 카테고리에 따라 키워드 검색
//        if (post.getCategory() == PostCategory.ALL) {
//            allKeywords = keywordRepository.findAll();
//        } else {
//            allKeywords = keywordRepository.findAllByPostCategory(post.getCategory());
//        }
//
//        // 키워드별로 알림 체크
//        allKeywords.forEach(keyword -> {
//            // 게시글 제목이나 내용에 키워드가 포함되어 있는지 확인
//            if (isKeywordMatched(post, keyword)) {
//                // 키워드 알림 생성
//                KeywordNotification keywordNotification = KeywordNotification.builder()
//                        .memberId(keyword.getMemberId())
//                        .relatedPostId(post.getId())
//                        .keyword(keyword.getKeyword())
//                        .postCategory(keyword.getPostCategory())
//                        .isRead(false)
//                        .content(createNotificationContent(post, keyword))
//                        .build();
//
//                // 알림 저장
//                keywordNotificationRepository.save(keywordNotification);
//
//                // SSE로 실시간 알림 전송
//                sseEmitterService.sendKeywordNotification(
//                        keyword.getMemberId().toString(),
//                        KeywordNotificationResponse.from(keywordNotification)
//                );
//            }
//        });
//    }
//
//    private boolean isKeywordMatched(Post post, Keyword keyword) {
//
//        // 제목이나 해쉬태그에 키워드가 포함되어 있는지 확인 (대소문자 구분 없이)
//        String searchContent = (post.getTitle() + " " + hashtagService.getHashtagsByPostId(post.getId())).toLowerCase();
//        return searchContent.contains(keyword.getKeyword().toLowerCase());
//    }
//
//    private String createNotificationContent(Post post, Keyword keyword) {
//        return String.format("'%s' A post related to the keyword has been published : %s",
//                keyword.getKeyword(),
//                post.getTitle()
//        );
//    }
//
//    // 키워드 알림 조회
//    public List<KeywordNotificationResponse> getNotificationsByMember(Long memberId) {
//        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
//        return keywordNotificationRepository.findByMemberIdAndIsReadFalseAndCreatedAtAfterOrderByCreatedAtDesc(
//                        memberId,
//                        startDate
//                )
//                .stream()
//                .map(KeywordNotificationResponse::from)
//                .collect(Collectors.toList());
//    }
//
//    // 알림 읽음 처리
//    public void markAsRead(Long notificationId) {
//        KeywordNotification keywordNotification = keywordNotificationRepository.findById(notificationId)
//                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
//        keywordNotification.markAsRead();
//    }
//}