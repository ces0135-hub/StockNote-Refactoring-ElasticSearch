package org.com.stocknote.domain.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchConditionDto {
    private String keyword;
    private SearchType searchType = SearchType.ALL;
    private String category;

    public String getCategory() {
        return category != null ? category.toUpperCase() : null;
    }

    public enum SearchType {
        TITLE, CONTENT, HASHTAG, USERNAME, ALL
    }
}
