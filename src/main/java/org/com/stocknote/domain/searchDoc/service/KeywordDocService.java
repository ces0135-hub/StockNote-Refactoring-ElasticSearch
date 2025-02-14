package org.com.stocknote.domain.searchDoc.service;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.keyword.dto.KeywordRequest;
import org.com.stocknote.domain.searchDoc.document.KeywordDoc;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.com.stocknote.domain.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordDocService {
    private final ElasticsearchOperations elasticsearchOperations;

    @Transactional
    public void updateKeywords(Member member, KeywordRequest request) {
        deleteAllKeywordsByMemberId(member.getId());
        saveNewKeywords(member, request);
    }

    private void deleteAllKeywordsByMemberId(Long memberId) {
        try {
            Query deleteQuery = NativeQuery.builder()
                    .withQuery(q -> q
                            .term(t -> t
                                    .field("member_id")
                                    .value(memberId)
                            )
                    )
                    .build();

            elasticsearchOperations.delete(deleteQuery, KeywordDoc.class, IndexCoordinates.of("stocknote_keyword"));
            elasticsearchOperations.indexOps(KeywordDoc.class).refresh();
        } catch (Exception e) {
            log.error("Failed to delete keywords for memberId: {}", memberId, e);
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }
    }

    private void saveNewKeywords(Member member, KeywordRequest request) {
        List<KeywordDoc> keywordDocs = request.getKeywords().stream()
                .map(keywordDto -> KeywordDoc.builder()
                        .memberId(member.getId())
                        .keyword(keywordDto.getKeyword())
                        .postCategory(keywordDto.getPostCategory())
                        .build())
                .collect(Collectors.toList());

        try {
            elasticsearchOperations.save(keywordDocs);
            elasticsearchOperations.indexOps(KeywordDoc.class).refresh();
        } catch (Exception e) {
            log.error("Failed to save new keywords", e);
            throw new RuntimeException("Failed to save new keywords", e);
        }
    }
}
