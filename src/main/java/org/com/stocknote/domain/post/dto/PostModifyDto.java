package org.com.stocknote.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.com.stocknote.domain.comment.entity.Comment;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;

import java.util.List;

@Getter
@Setter
public class PostModifyDto {
    @NotBlank(message = "제목은 필수로 입력해주세요")
    private String title;

    @NotBlank(message = "내용을 필수로 입력해주세요")
    private String content;

    private List<String> hashtags;

    private String category;

    public Post toEntity(Long id, Member member) {
        PostCategory postCategory = PostCategory.valueOf(category);

        return Post.builder()
                .id(id)
                .title(this.title)
                .body(this.content)
                .category(postCategory)
                .member(member)
                .build();
    }
}
