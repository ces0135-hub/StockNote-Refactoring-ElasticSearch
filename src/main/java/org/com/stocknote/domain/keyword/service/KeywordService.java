package org.com.stocknote.domain.keyword.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.keyword.dto.KeywordRequest;
import org.com.stocknote.domain.keyword.dto.KeywordResponse;
import org.com.stocknote.domain.keyword.entity.Keyword;
import org.com.stocknote.domain.keyword.repository.KeywordRepository;
import org.com.stocknote.domain.member.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;

    @Transactional
    public KeywordResponse updateKeywords(Member member, KeywordRequest request) {
        // 해당 멤버의 기존 키워드 삭제
        keywordRepository.deleteByMemberId(member.getId());

        // 새 키워드 저장
        List<Keyword> newKeywords = request.getKeywords().stream()
                .map(keywordDto -> Keyword.builder()
                        .keyword(keywordDto.getKeyword())
                        .postCategory(keywordDto.getPostCategory())
                        .memberId(member.getId())
                        .build())
                .collect(Collectors.toList());

        List<Keyword> savedKeywords = keywordRepository.saveAll(newKeywords);

        List<KeywordResponse.KeywordDto> keywordDtos = savedKeywords.stream()
                .map(keyword -> KeywordResponse.KeywordDto.builder()
                        .keyword(keyword.getKeyword())
                        .postCategory(keyword.getPostCategory())
                        .build())
                .collect(Collectors.toList());

        return KeywordResponse.builder()
                .keywords(keywordDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public KeywordResponse getKeywords(Member member) {
        List<Keyword> memberKeywords = keywordRepository.findByMemberId(member.getId());

        List<KeywordResponse.KeywordDto> keywordDtos = memberKeywords.stream()
                .map(keyword -> KeywordResponse.KeywordDto.builder()
                        .keyword(keyword.getKeyword())
                        .postCategory(keyword.getPostCategory())
                        .build())
                .collect(Collectors.toList());

        return KeywordResponse.builder()
                .keywords(keywordDtos)
                .build();
    }
}
