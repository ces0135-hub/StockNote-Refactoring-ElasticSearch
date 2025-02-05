package org.com.stocknote.domain.stockVote.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.stockVote.dto.StockVoteListResponse;
import org.com.stocknote.domain.stockVote.dto.StockVoteRequest;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stockVote.dto.StockVoteResponse;
import org.com.stocknote.domain.stockVote.entity.StockVote;
import org.com.stocknote.domain.stockVote.entity.VoteStatistics;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.com.stocknote.domain.stockVote.repository.StockVoteRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockVoteService {
    private final StockVoteRepository stockVoteRepository;
    private final StockRepository stockRepository;
    private final MemberRepository memberRepository;
    private final StringRedisTemplate redisTemplate;

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

        String key = "vote:" + stockCode;
        if(voteType == VoteType.BUY){
            redisTemplate.opsForHash().increment(key, "buy", 1);
        }else {
            redisTemplate.opsForHash().increment(key, "sell", 1);
        }
    }

    @Transactional(readOnly = true)
    public VoteStatistics getVoteStatistics(String stockCode) {
        String key = "vote:" + stockCode;
        int buyVotes = redisTemplate.opsForHash().get(key, "buy") != null
                ? Integer.parseInt(redisTemplate.opsForHash().get(key, "buy").toString())
                : 0;
        int sellVotes = redisTemplate.opsForHash().get(key, "sell") != null
                ? Integer.parseInt(redisTemplate.opsForHash().get(key, "sell").toString())
                : 0;
        int totalVotes = buyVotes + sellVotes;


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

    @Transactional(readOnly = true)
    public StockVoteListResponse getPopularVote () {
        Set<String> keys = redisTemplate.keys("vote:*");
        if (keys == null || keys.isEmpty()) {
            return new StockVoteListResponse(Collections.emptyList());
        }

        List<StockVoteResponse> voteResponses = keys.stream()
                .map(key -> {
                    String stockCode = key.replace("vote:", "");
                    String stockName = stockRepository.findStockNameByCode(stockCode);
                    Object buyVotesObj = redisTemplate.opsForHash().get(key, "buy");
                    Object sellVotesObj = redisTemplate.opsForHash().get(key, "sell");

                    int buyVotes = (buyVotesObj != null) ? Integer.parseInt(buyVotesObj.toString()) : 0;
                    int sellVotes = (sellVotesObj != null) ? Integer.parseInt(sellVotesObj.toString()) : 0;
                    int totalVotes = buyVotes + sellVotes;

                    return new StockVoteResponse(
                            stockName,
                            stockCode,
                            totalVotes,
                            totalVotes > 0 ? (buyVotes * 100.0 / totalVotes) : 0,
                            totalVotes > 0 ? (sellVotes * 100.0 / totalVotes) : 0
                    );
                })
                .sorted(Comparator.comparingLong(StockVoteResponse::getTotalVotes).reversed()) // 총 투표수 기준 정렬
                .limit(3)
                .collect(Collectors.toList());

        return new StockVoteListResponse(voteResponses);
    }

    @Getter
    public enum VoteType {
        BUY, SELL
    }
}
