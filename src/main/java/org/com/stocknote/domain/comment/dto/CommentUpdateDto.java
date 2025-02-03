package org.com.stocknote.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentUpdateDto {
    private Long commentId;
    private Long postId;
    private String body;
}
