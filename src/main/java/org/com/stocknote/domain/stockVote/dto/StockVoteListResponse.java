package org.com.stocknote.domain.stockVote.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class StockVoteListResponse {
    public final List<StockVoteResponse> stockVoteList;
}
