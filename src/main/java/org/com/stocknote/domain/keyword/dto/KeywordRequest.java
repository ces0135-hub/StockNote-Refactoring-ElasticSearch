package org.com.stocknote.domain.keyword.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.stocknote.domain.keyword.entity.Keyword;
import org.com.stocknote.domain.post.entity.PostCategory;

import java.util.List;

@Getter
@NoArgsConstructor
public class KeywordRequest {
    private List<KeywordDto> keywords;

    @Builder
    public KeywordRequest(List<KeywordDto> keywords) {
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
