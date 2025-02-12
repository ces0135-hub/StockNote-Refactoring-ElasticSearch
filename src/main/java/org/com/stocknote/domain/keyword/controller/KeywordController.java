package org.com.stocknote.domain.keyword.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.keyword.dto.KeywordRequest;
import org.com.stocknote.domain.keyword.dto.KeywordResponse;
import org.com.stocknote.domain.keyword.service.KeywordService;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.searchDoc.service.KeywordDocService;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "키워드 API", description = "유저별 키워드 조회/업데이트 API")
public class KeywordController {

    private final KeywordService keywordService;
    private final KeywordDocService keywordDocService;

    // 키워드 전체 조회
    @GetMapping("/keywords")
    @Operation(summary = "키워드 목록 조회")
    public ResponseEntity<KeywordResponse> getKeywords(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Member member = principalDetails.user();
        KeywordResponse response = keywordService.getKeywords(member);
        return ResponseEntity.ok(response);
    }

    // 키워드 전체 업데이트
    @PutMapping("/keywords")
    @Operation(summary = "키워드 업데이트")
    public ResponseEntity<KeywordResponse> updateKeywords(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody KeywordRequest request
    ) {
        Member member = principalDetails.user();
        KeywordResponse response = keywordService.updateKeywords(member, request);
        keywordDocService.save(member, request);
        return ResponseEntity.ok(response);
    }
}
