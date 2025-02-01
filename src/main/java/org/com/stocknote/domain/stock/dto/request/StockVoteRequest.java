package org.com.stocknote.domain.stock.dto.request;

import lombok.Data;
import lombok.Getter;
import org.com.stocknote.domain.stock.entity.VoteType;

@Getter
@Data
public class StockVoteRequest {
    private boolean sell;
    private boolean buy;

    public VoteType getVoteType () {
        if (this.buy) {
            return VoteType.BUY;
        } else if (this.sell) {
            return VoteType.SELL;
        } else {
            return null;
        }
    }
}
