package org.com.stocknote.domain.post.dto;

import org.com.stocknote.domain.comment.dto.CommentDetailResponse;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;

import java.time.LocalDateTime;
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
        List<String>hashtags
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
                hashtags
        );
    }
}

