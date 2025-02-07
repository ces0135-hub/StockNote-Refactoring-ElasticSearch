package org.com.stocknote.domain.searchDoc.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.searchDoc.document.StockDoc;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.searchDoc.repository.StockDocRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockDocService {
  private final StockDocRepository stockDocRepository;

  public List<StockDoc> searchStocks(String keyword) {
    return stockDocRepository.searchByKeyword(keyword);
  }
}
