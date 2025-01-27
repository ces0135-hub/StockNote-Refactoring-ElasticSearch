package org.com.stocknote.domain.comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import org.com.stocknote.global.base.BaseEntity;

@Entity
@Getter
public class Comment extends BaseEntity {

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private Long userId;

    public Comment(Long postId, String body, Long userId) {
        this.postId = postId;
        this.body = body;
        this.userId = userId;
    }

    public Comment() {

    }

//    public void setBody(String body) {
//        this.body = body;
//    }
}
