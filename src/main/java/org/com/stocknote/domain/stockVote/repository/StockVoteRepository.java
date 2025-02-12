package org.com.stocknote.domain.stockVote.repository;

import org.com.stocknote.domain.stockVote.entity.StockVote;
import org.com.stocknote.domain.stockVote.service.StockVoteService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface StockVoteRepository extends JpaRepository<StockVote, Long> {
    void deleteAllByVoteDateBefore (LocalDate date);
}
