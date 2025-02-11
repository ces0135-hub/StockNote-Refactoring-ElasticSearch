package org.com.stocknote.domain.stockVote.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VoteStatistics {
    private long totalVotes;
    private double buyPercentage;
    private double sellPercentage;
}
