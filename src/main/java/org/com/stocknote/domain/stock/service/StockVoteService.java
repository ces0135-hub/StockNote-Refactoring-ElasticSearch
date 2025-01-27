package org.com.stocknote.domain.stock.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stock.entity.StockVote;
import org.com.stocknote.domain.stock.entity.VoteStatistics;
import org.com.stocknote.domain.stock.entity.VoteType;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.com.stocknote.domain.stock.repository.StockVoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class StockVoteService {
    private final StockVoteRepository stockVoteRepository;
    private final StockRepository stockRepository;

    public void vote(String stockCode, VoteType voteType) {
        // 오늘 날짜의 이전 투표 확인 및 삭제
//        LocalDate today = LocalDate.now();
//        stockVoteRepository.deleteStockCodeAndVoteDate( stockCode, today);

        // 새로운 투표 저장
        Stock stock = stockRepository.findById(stockCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 종목입니다."));

        StockVote vote = StockVote.builder()
                .stock(stock)
//                .userId(userId)
                .voteType(voteType)
                .build();

        stockVoteRepository.save(vote);
    }

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
}
