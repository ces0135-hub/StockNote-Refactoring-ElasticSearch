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
public class PostModifyDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    private List<String> hashtags;

    private String category;


    public Post toEntity(Long id, Member member) {
        PostCategory postCategory = PostCategory.valueOf(category);

        return Post.builder()
                .id(id)
                .title(this.title)
                .body(this.body)
                .category(postCategory)
                .member(member)
                .build();
    }
}