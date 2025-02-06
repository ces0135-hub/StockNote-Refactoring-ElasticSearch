package org.com.stocknote.domain.stockDoc.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stockDoc.repository.StockDocRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockDocService {
  private final StockDocRepository stockDocRepository;

  public List<Stock> searchStocks(String keyword) {
    return stockDocRepository.searchByKeyword(keyword);
  }
}
