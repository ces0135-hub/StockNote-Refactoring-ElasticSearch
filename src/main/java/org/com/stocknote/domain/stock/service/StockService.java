package org.com.stocknote.domain.stock.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.memberStock.entity.MemberStock;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.memberStock.repository.MemberStockRepository;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.com.stocknote.domain.stock.service.price.StockPriceProcessor;
import org.com.stocknote.domain.stockApi.dto.response.StockInfoResponse;
import org.com.stocknote.domain.stockApi.dto.response.StockPriceResponse;
import org.com.stocknote.domain.stockApi.dto.response.StockResponse;
import org.com.stocknote.domain.stockApi.service.StockApiService;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.com.stocknote.websocket.service.WebSocketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StockService {
    private final WebSocketService webSocketService;
    private final MemberRepository memberRepository;
    private final StockRepository stockRepository;
    private final MemberStockRepository memberStockRepository;
    private final StockApiService stockApiService;
    private final StockPriceProcessor priceProcessor;

    @Transactional(readOnly = true)
    public StockInfoResponse findStock(String name) {
        return stockRepository.findByName(name)
                .map(StockInfoResponse::of)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.STOCK_NOT_FOUND.getMessage()));
    }

    @Transactional(readOnly = true)
    public List<Stock> searchStocks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String searchKeyword = keyword.toLowerCase();
        return stockRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(searchKeyword,
            searchKeyword);
    }

    @Transactional
    public void addStock(String name, String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Stock stock = stockRepository.findByName(name)
                .orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));
        if (memberStockRepository.existsByMemberAndStock(member, stock)) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_STOCK);
        }
        MemberStock memberStock = MemberStock.create(member, stock);
        memberStockRepository.save(memberStock);
    }

    @Transactional(readOnly = true)
    public List<StockResponse> getMyStocks(String email) {
        List<MemberStock> memberStocks = memberStockRepository
                .findByMemberEmailOrderByAddedAtDesc(email);

        return memberStocks.stream()
                .map(memberStock -> {
                    String stockCode = memberStock.getStock().getCode();
                    Stock stock = memberStock.getStock();

                    try {
                        StockPriceResponse priceResponse = stockApiService.getStockPrice(stockCode).block();

                        Optional<StockResponse> stockResponse = priceProcessor
                                .processStockPriceResponse(priceResponse, stockCode, stock, memberStock);

                        if (stockResponse.isPresent()) {
                            // WebSocket 구독 시작
                            webSocketService.subscribeStockPrice(stockCode);
                            return stockResponse.get();
                        }
                    } catch (Exception e) {
                        log.error("❌ Failed to fetch stock price for {}: {}", stockCode, e.getMessage());
                    }

                    // 가격 정보를 가져오지 못한 경우 기본 정보만 반환
                    return StockResponse.of(stock, memberStock);
                })
                .collect(Collectors.toList());
    }

    public void deleteStock (String code, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Stock stock = stockRepository.findByCode(code)
                .orElseThrow(() -> new CustomException(ErrorCode.STOCK_NOT_FOUND));
        MemberStock memberStock = memberStockRepository.findByMemberAndStock(member, stock)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_STOCK_NOT_FOUND));
        memberStockRepository.delete(memberStock);
    }
}
