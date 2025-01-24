package org.com.stocknote.domain.portfolio.portfolioStock.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.portfolio.portfolioStock.repository.PfStockRepository;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PfStockService {
  private final PfStockRepository pfStockRepository;

  //임시
  private final StockRepository stockRepository;

  public List<PfStock> getStockList(Long portfolioNo) {
    return pfStockRepository.findByPortfolioId(portfolioNo);
  }

  public PfStock savePfStock(PfStock pfStock) {

    return pfStockRepository.save(pfStock);
  }

  public Stock saveTempStock(Stock stock) {
    return stockRepository.save(stock);
  }

  public Stock getTempStock(String n) {
    return stockRepository.findById(n).orElse(null);
  }


}
