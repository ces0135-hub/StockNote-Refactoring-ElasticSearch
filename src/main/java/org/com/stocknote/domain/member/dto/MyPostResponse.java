package org.com.stocknote.domain.member.dto;

import org.com.stocknote.domain.like.repository.LikeRepository;
import org.com.stocknote.domain.post.entity.Post;

import java.time.LocalDateTime;

public record MyPostResponse(
        Long id,
        String title,
        String body,
        LocalDateTime createdAt
) {

    public static MyPostResponse of(Post post) {
        return new MyPostResponse(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getCreatedAt()
        );
    }

}
