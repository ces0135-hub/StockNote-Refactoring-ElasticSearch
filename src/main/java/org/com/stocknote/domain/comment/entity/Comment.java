package org.com.stocknote.domain.comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.stocknote.global.base.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Comment extends BaseEntity {

    @Column(nullable = false)
    private Long postId;


    @Setter
    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private Long userId;

    public Comment(Long postId, String body, Long userId) {
        this.postId = postId;
        this.body = body;
        this.userId = userId;
    }

}
