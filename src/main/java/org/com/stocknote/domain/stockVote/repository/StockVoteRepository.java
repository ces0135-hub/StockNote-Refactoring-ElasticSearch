package org.com.stocknote.domain.stockVote.repository;

import org.com.stocknote.domain.stockVote.entity.StockVote;
import org.com.stocknote.domain.stockVote.service.StockVoteService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface StockVoteRepository extends JpaRepository<StockVote, Long> {

    long countByStockCodeAndVoteDate (String stockCode, LocalDate today);

    long countByStockCodeAndVoteDateAndVoteType (String stockCode, LocalDate today, StockVoteService.VoteType voteType);

    void deleteAllByVoteDateBefore (LocalDate date);
}
