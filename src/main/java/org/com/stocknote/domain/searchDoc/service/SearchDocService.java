package org.com.stocknote.domain.searchDoc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.searchDoc.document.PortfolioDoc;
import org.com.stocknote.domain.searchDoc.document.PortfolioStockDoc;
import org.com.stocknote.domain.searchDoc.document.PostDoc;
import org.com.stocknote.domain.searchDoc.document.StockDoc;
import org.com.stocknote.domain.searchDoc.repository.PortfolioDocRepository;
import org.com.stocknote.domain.searchDoc.repository.PortfolioStockDocRepository;
import org.com.stocknote.domain.searchDoc.repository.PostDocRepository;
import org.com.stocknote.domain.searchDoc.repository.StockDocRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchDocService {
  private final StockDocRepository stockDocRepository;
  private final PortfolioDocRepository portfolioDocRepository;
  private final PortfolioStockDocRepository portfolioStockDocRepository;
  private final MemberRepository memberRepository;
  private final PostDocRepository postDocRepository;

  public List<StockDoc> searchStocks(String keyword) {
    return stockDocRepository.searchByKeyword(keyword);
  }

  public PortfolioDoc getMyPortfolioList(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    PortfolioDoc portfolioDoc = portfolioDocRepository.findByMemberId(member.getId());

    return portfolioDoc;
  }

  public List<PortfolioStockDoc> getMyPortfolioStockList(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    List<PortfolioStockDoc> portfolioStockDocList = portfolioStockDocRepository.findByMemberId(member.getId());
    return portfolioStockDocList;
  }

  public Page<PostDoc> searchPosts(PostSearchConditionDto condition, Pageable pageable) {
    if (!StringUtils.hasText(condition.getKeyword())) {
      return Page.empty(pageable);
    }

    return switch (condition.getSearchType()) {
      case TITLE -> postDocRepository.searchByTitle(condition.getKeyword(), pageable);
      case CONTENT -> postDocRepository.searchByContent(condition.getKeyword(), pageable);
      case USERNAME -> postDocRepository.searchByUsername(condition.getKeyword(), pageable);
      case HASHTAG -> postDocRepository.searchByHashtag(condition.getKeyword(), pageable);
      case ALL -> postDocRepository.searchByAll(condition.getKeyword(), pageable);
    };
  }
}
