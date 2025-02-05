package org.com.stocknote.domain.stockVote.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.stockVote.dto.StockVoteRequest;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stockVote.entity.StockVote;
import org.com.stocknote.domain.stockVote.entity.VoteStatistics;
import org.com.stocknote.domain.stock.type.VoteType;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.com.stocknote.domain.stockVote.repository.StockVoteRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StockVoteService {
    private final StockVoteRepository stockVoteRepository;
    private final StockRepository stockRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void vote(String stockCode, StockVoteRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Stock stock = stockRepository.findById(stockCode)
                .orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));
        VoteType voteType = request.getVoteType();
        StockVote vote = StockVote.builder()
                .stock(stock)
                .userId(member.getEmail())
                .voteType(voteType)
                .build();

        stockVoteRepository.save(vote);
    }

    @Transactional(readOnly = true)
    public VoteStatistics getVoteStatistics(String stockCode) {
        LocalDate today = LocalDate.now();
        long totalVotes = stockVoteRepository.countByStockCodeAndVoteDate(stockCode, today);
        long buyVotes = stockVoteRepository.countByStockCodeAndVoteDateAndVoteType(
                stockCode, today, VoteType.BUY);

        return VoteStatistics.builder()
                .totalVotes(totalVotes)
                .buyPercentage(totalVotes > 0 ? (buyVotes * 100.0 / totalVotes) : 0)
                .sellPercentage(totalVotes > 0 ? ((totalVotes - buyVotes) * 100.0 / totalVotes) : 0)
                .build();
    }
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    @Transactional
    public void resetVotes() {
        stockVoteRepository.deleteAllByVoteDateBefore(LocalDate.now()); // 오늘 이전의 투표 삭제
    }

}
