package org.com.stocknote.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;

import java.util.List;

@Getter
@Setter
public class PostCreateDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private List<String> hashtags;

    private String category;

    public Post toEntity(Member member) {
        PostCategory postCategory = PostCategory.valueOf(category);
        return Post.builder()
                .member(member)
                .title(this.title)
                .body(this.content)
                .category(postCategory)
                .build();
    }
}