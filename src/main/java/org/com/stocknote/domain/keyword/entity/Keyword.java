package org.com.stocknote.domain.keyword.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.global.base.BaseEntity;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Keyword extends BaseEntity {
    private String keyword;

    @Enumerated(EnumType.STRING)
    private PostCategory postCategory;

    private Long memberId;
}