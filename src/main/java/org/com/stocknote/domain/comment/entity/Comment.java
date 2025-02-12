package org.com.stocknote.domain.comment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.global.base.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Setter
    @Column(nullable = false, length = 500) // 최대 길이 제한
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    public Comment(Post post, String body, Member member) {
        this.post = post;
        this.body = body;
        this.member = member;
    }
}
