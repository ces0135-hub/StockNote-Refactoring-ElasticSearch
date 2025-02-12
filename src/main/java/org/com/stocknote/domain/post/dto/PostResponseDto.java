package org.com.stocknote.domain.post.dto;

import org.com.stocknote.domain.comment.dto.CommentDetailResponse;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.searchDoc.document.PostDoc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record PostResponseDto(
        Long id,
        String title,
        String body,
        Long authorId,
        String username,
        String profile,
        List<CommentDetailResponse> comments,
        LocalDateTime createdAt,
        PostCategory category,
        List<String> hashtags,
        int likeCount,
        int commentCount
) {
    public static PostResponseDto fromPost(Post post, List<String> hashtags) {
        List<CommentDetailResponse> commentResponses = post.getComments().stream()
                .map(CommentDetailResponse::of)
                .toList();
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getMember().getId(),
                post.getMember().getName(),
                post.getMember().getProfile(),
                commentResponses,
                post.getCreatedAt(),
                post.getCategory(),
                hashtags,
                post.getLikes().size(),
                post.getComments().size()
        );
    }

    // 지연 로딩용 메서드 추가
    public List<CommentDetailResponse> getComments() {
        if (comments == null) {
            // 실제 댓글 로딩 로직 (서비스 레이어에서 처리)
            return Collections.emptyList();
        }
        return comments;
    }

    public static PostResponseDto fromPost(PostDoc postDoc) {
        return new PostResponseDto(
            Long.valueOf(postDoc.getId()),
            postDoc.getTitle(),
            postDoc.getBody(),
            Long.valueOf(postDoc.getMemberDoc().getId()),
            postDoc.getMemberDoc().getName(),
            postDoc.getMemberDoc().getProfile(),
                null,
//            LocalDateTime.parse(postDoc.getCreatedAt()),
            null,
            postDoc.getCategory(),
                null,
            postDoc.getLikeCount(),
            postDoc.getCommentCount()
        );
    }

    public Post toEntity(Member member) {
        return Post.builder()
                .title(this.title)
                .body(this.body)
                .category(this.category)
                .member(member)       // 연관관계 설정
                .comments(new ArrayList<>())  // 빈 리스트로 초기화
                .likes(new ArrayList<>())     // 빈 리스트로 초기화
                .build();
    }

}
