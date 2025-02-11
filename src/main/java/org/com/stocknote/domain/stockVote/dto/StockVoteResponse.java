package org.com.stocknote.domain.stockVote.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockVoteResponse {
    private final String stockName;
    private final String stockCode;
    private final long totalVotes;
    private final double buyPercentage;
    private final double sellPercentage;
}
