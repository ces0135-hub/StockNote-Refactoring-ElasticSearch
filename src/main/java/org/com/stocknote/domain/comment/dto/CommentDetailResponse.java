package org.com.stocknote.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CommentDetailResponse {
    private Long commentId;
    private String body;
    private String author;
    private LocalDateTime createdAt;
}
