package org.com.stocknote.domain.stockVote.dto;

import lombok.Data;
import lombok.Getter;
import org.com.stocknote.domain.stockVote.service.StockVoteService;

@Getter
@Data
public class StockVoteRequest {
    private boolean sell;
    private boolean buy;

    public StockVoteService.VoteType getVoteType() {
        if (this.buy) {
            return StockVoteService.VoteType.BUY;
        } else if (this.sell) {
            return StockVoteService.VoteType.SELL;
        } else {
            return null;
        }
    }
}
