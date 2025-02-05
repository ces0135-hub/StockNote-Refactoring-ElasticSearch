package org.com.stocknote.domain.notification.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.comment.entity.Comment;
import org.com.stocknote.domain.notification.dto.NotificationResponse;
import org.com.stocknote.domain.notification.entity.Notification;
import org.com.stocknote.domain.notification.repository.NotificationRepository;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;
    private final SseEmitterService sseEmitterService;

    public void createCommentNotification(Long postId, Comment comment) {
        Post post = postRepository.findById(postId).orElseThrow();
        // 게시글 작성자가 댓글 작성자와 다를 경우에만 알림 생성
        if (!post.getMember().equals(comment.getMember())) {
            Notification notification = Notification.builder()
                    .memberId(post.getMember().getId())
                    .relatedPostId(post.getId())
                    .relatedCommentId(comment.getId())
                    .isRead(false)
                    .content(comment.getMember().getName() + "님이 댓글을 남겼습니다.")
                    .build();

            notificationRepository.save(notification);

            // SSE로 실시간 알림 전송
            sseEmitterService.sendNotification(
                    post.getMember().getId().toString(),
                    NotificationResponse.from(notification)
            );
        }
    }

    public List<NotificationResponse> getNotificationsByMember(Member member) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        return notificationRepository.findByMemberIdAndIsReadFalseAndCreatedAtAfterOrderByCreatedAtDesc(
                        member,
                        startDate
                ).stream()
                .map(notification -> NotificationResponse.from(notification))
                // 또는 메서드 레퍼런스를 사용: .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
        notification.markAsRead();
    }

}
