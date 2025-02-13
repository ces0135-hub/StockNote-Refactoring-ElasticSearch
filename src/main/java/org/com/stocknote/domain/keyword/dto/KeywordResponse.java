package org.com.stocknote.domain.keyword.dto;

import lombok.*;
import org.com.stocknote.domain.post.entity.PostCategory;

import java.util.List;

@Getter
@NoArgsConstructor
public class KeywordResponse {
    private List<KeywordDto> keywords;

    @Builder
    public KeywordResponse(List<KeywordDto> keywords) {
        this.keywords = keywords;
    }

    @Getter
    @NoArgsConstructor
    public static class KeywordDto {
        private String keyword;
        private PostCategory postCategory;

        @Builder
        public KeywordDto(String keyword, PostCategory postCategory) {
            this.keyword = keyword;
            this.postCategory = postCategory;
        }
    }
}
