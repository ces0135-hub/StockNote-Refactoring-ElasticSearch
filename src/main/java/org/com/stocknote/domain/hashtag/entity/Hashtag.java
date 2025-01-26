package org.com.stocknote.domain.hashtag.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.global.base.BaseEntity;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hashtags")
public class Hashtag extends BaseEntity {
    @Column(nullable = false)
    private String name;

    private Long postId;

    public static Hashtag create(String name, Long postId) {
        return Hashtag.builder()
                .name(name)
                .postId(postId)
                .build();
    }
}