package org.com.stocknote.domain.searchDoc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.searchDoc.document.MemberDoc;
import org.com.stocknote.domain.searchDoc.document.PortfolioDoc;
import org.com.stocknote.domain.searchDoc.document.PortfolioStockDoc;
import org.com.stocknote.domain.searchDoc.document.StockDoc;
import org.com.stocknote.domain.searchDoc.repository.PortfolioDocRepository;
import org.com.stocknote.domain.searchDoc.repository.PortfolioStockDocRepository;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.searchDoc.repository.StockDocRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockDocService {
  private final StockDocRepository stockDocRepository;
  private final PortfolioDocRepository portfolioDocRepository;
  private final PortfolioStockDocRepository portfolioStockDocRepository;
  private final MemberRepository memberRepository;

  public List<StockDoc> searchStocks(String keyword) {
    return stockDocRepository.searchByKeyword(keyword);
  }

  public PortfolioDoc getMyPortfolioList(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    PortfolioDoc portfolioDoc = portfolioDocRepository.findByMemberId(member.getId());
    log.debug("portfolioDoc========================: {}", portfolioDoc);

    return portfolioDoc;
  }

  public List<PortfolioStockDoc> getMyPortfolioStockList(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    List<PortfolioStockDoc> portfolioStockDocList = portfolioStockDocRepository.findByMemberId(member.getId());
    log.debug("portfolioStockDocList============================: {}", portfolioStockDocList);
    return portfolioStockDocList;
  }
}
