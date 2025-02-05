package org.com.stocknote.domain.post.dto;

import lombok.Getter;
import lombok.Setter;
import org.com.stocknote.domain.post.entity.PostCategory;

@Getter
@Setter
public class PostSearchConditionDto {
    private String keyword;
    private SearchType searchType;
    private PostCategory category;

    public enum SearchType {
        TITLE, CONTENT, HASHTAG, USERNAME, ALL
    }
}
