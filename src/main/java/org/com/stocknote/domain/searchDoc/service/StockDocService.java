package org.com.stocknote.domain.searchDoc.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.searchDoc.document.StockDoc;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.searchDoc.repository.StockDocRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true", matchIfMissing = false)
public class StockDocService {
  private final StockDocRepository stockDocRepository;

  public List<StockDoc> searchStocks(String keyword) {
    return stockDocRepository.searchByKeyword(keyword);
  }
}
