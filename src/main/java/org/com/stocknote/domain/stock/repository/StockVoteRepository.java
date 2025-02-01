package org.com.stocknote.domain.stock.repository;

import org.com.stocknote.domain.stock.entity.StockVote;
import org.com.stocknote.domain.stock.type.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface StockVoteRepository extends JpaRepository<StockVote, Long> {

    long countByStockCodeAndVoteDate (String stockCode, LocalDate today);

    long countByStockCodeAndVoteDateAndVoteType (String stockCode, LocalDate today, VoteType voteType);

}
