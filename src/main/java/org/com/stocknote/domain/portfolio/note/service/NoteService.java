package org.com.stocknote.domain.portfolio.note.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.portfolio.note.entity.Note;
import org.com.stocknote.domain.portfolio.note.repository.NoteRepository;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.request.PfStockPatchRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {
  private final String BUY = "매수";
  private final String SELL = "매도";
  private final String UPDATE = "수정";
  private final String DELETE = "삭제";

  private final NoteRepository noteRepository;
  private final SecurityUtils securityUtils;

  public Note buyStock(Portfolio portfolio, PfStock pfStock, PfStockPatchRequest pfStockPatchRequest) {
    Stock stock = pfStock.getStock();
    Member member = securityUtils.getCurrentMember();
    Note note = Note.builder()
      .type(BUY)
      .stock(stock)
      .amount(pfStockPatchRequest.getPfstockCount())
      .price(pfStockPatchRequest.getPfstockPrice())
      .portfolio(portfolio)
      .member(member)
      .build();

    return note;
    }


  public Note sellStock(Portfolio portfolio, PfStock pfStock, PfStockPatchRequest pfStockPatchRequest) {
    Stock stock = pfStock.getStock();
    Member member = securityUtils.getCurrentMember();
    Note note = Note.builder()
      .type(SELL)
      .stock(stock)
      .amount(pfStockPatchRequest.getPfstockCount())
      .price(pfStockPatchRequest.getPfstockPrice())
      .portfolio(portfolio)
      .member(member)
      .build();

    return note;
  }

  public Note updateStock(Portfolio portfolio, PfStock pfStock, PfStockPatchRequest pfStockPatchRequest) {
    Stock stock = pfStock.getStock();
    Member member = securityUtils.getCurrentMember();
    Note note = Note.builder()
      .type(UPDATE)
      .stock(stock)
      .amount(pfStockPatchRequest.getPfstockCount())
      .price(pfStockPatchRequest.getPfstockPrice())
      .portfolio(portfolio)
      .member(member)
      .build();

    return note;
  }

  public Note deleteStock(Portfolio portfolio, PfStock pfStock) {
    Stock stock = pfStock.getStock();
    Member member = securityUtils.getCurrentMember();
    Note note = Note.builder()
      .type(DELETE)
      .stock(stock)
      .amount(pfStock.getPfstockCount())
      .price(pfStock.getPfstockPrice())
      .portfolio(portfolio)
      .member(member)
      .build();

    return note;
  }

  public List<Note> getNoteByPortfolioNo(Long portfolioNo) {
    return noteRepository.findByPortfolioId(portfolioNo);
  }

  public List<Note> getNoteList() {
    return noteRepository.findAll();
  }
}
