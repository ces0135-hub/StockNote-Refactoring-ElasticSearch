package org.com.stocknote.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.com.stocknote.domain.comment.entity.Comment;

import java.time.LocalDateTime;

public record CommentDetailResponse(
        Long id,
        String body,
        LocalDateTime createdAt,
        Long authorId,
        String author,
        String profile
) {
    // Factory method to convert Comment to DTO
    public static CommentDetailResponse of(Comment comment) {
        return new CommentDetailResponse(
                comment.getId(),
                comment.getBody(),
                comment.getCreatedAt(),
                comment.getMember().getId(),
                comment.getMember().getName(),
                comment.getMember().getProfile()
        );
    }
}
