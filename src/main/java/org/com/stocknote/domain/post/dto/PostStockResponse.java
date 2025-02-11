package org.com.stocknote.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.stocknote.domain.post.entity.Post;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostStockResponse {
    private Long id;                     // 게시글 ID
    private String title;                // 게시글 제목
    private String content;              // 게시글 내용
    private String authorName;           // 작성자 이름
    private String profile;   // 작성자 프로필 이미지
    private LocalDateTime createdAt;     // 작성일시
    private int likeCount;               // 좋아요 수
    private int commentCount;            // 댓글 수

    public static PostStockResponse from(Post post) {
        return PostStockResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getBody())
                .authorName(post.getMember().getName())
                .profile(post.getMember().getProfile())
                .createdAt(post.getCreatedAt())
                .likeCount(post.getLikes().size())
                .commentCount(post.getComments().size())
                .build();
    }
}
