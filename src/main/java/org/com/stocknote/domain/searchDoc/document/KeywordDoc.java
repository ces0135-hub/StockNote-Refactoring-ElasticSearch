package org.com.stocknote.domain.searchDoc.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.com.stocknote.domain.keyword.entity.Keyword;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.springframework.data.elasticsearch.annotations.*;

@Document(indexName = "stocknote_keyword")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordDoc {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String keyword;

    @Field(type = FieldType.Long, name = "member_id")
    private Long memberId;

    @Field(type = FieldType.Keyword, name="post_category")
    private PostCategory postCategory;

    public static KeywordDoc from(Keyword keyword) {
        return KeywordDoc.builder()
                .id(String.valueOf(keyword.getId()))
                .keyword(keyword.getKeyword())
                .memberId(keyword.getMemberId())
                .postCategory(keyword.getPostCategory())
                .build();
    }
}

