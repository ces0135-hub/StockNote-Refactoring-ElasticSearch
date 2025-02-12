package org.com.stocknote.domain.member.dto;

import org.com.stocknote.domain.comment.entity.Comment;
import java.time.LocalDateTime;

public record MyCommentResponse(
        Long id,
        String body,
        LocalDateTime createdAt
) {
    public static MyCommentResponse of(Comment comment) {
        return new MyCommentResponse(
                comment.getId(),
                comment.getBody(),
                comment.getCreatedAt()
        );
    }

}
