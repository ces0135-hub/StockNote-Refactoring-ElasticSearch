package org.com.stocknote.domain.stockVote.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stockVote.dto.StockVoteRequest;
import org.com.stocknote.domain.stockVote.entity.VoteStatistics;
import org.com.stocknote.domain.stockVote.service.StockVoteService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vote")
@Tag(name = "투표 API", description = "종목 투표 관련 API")
public class stockVoteController {
    private final StockVoteService stockVoteService;

    @PostMapping("/{stockCode}/vote")
    @Operation(summary = "종목 투표")
    public GlobalResponse vote(
            @PathVariable String stockCode,
            @RequestBody StockVoteRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String email = principalDetails.getUsername();
        stockVoteService.vote(stockCode, request, email);
        return GlobalResponse.success();
    }

    @GetMapping("/{stockCode}/vote-statistics")
    @Operation(summary = "종목 투표 통계 조회")
    public GlobalResponse<VoteStatistics> getVoteStatistics(
            @PathVariable String stockCode) {
        return GlobalResponse.success(stockVoteService.getVoteStatistics(stockCode));
    }
}
