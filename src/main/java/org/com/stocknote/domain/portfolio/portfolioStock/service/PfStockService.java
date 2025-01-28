package org.com.stocknote.domain.portfolio.portfolioStock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.PfStockPatchRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.PfStockRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.portfolio.portfolioStock.repository.PfStockRepository;
import org.com.stocknote.domain.stock.dto.response.StockPriceResponse;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.com.stocknote.domain.stock.service.StockService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class PfStockService {
  private final PfStockRepository pfStockRepository;
  private final PortfolioService portfolioService;
  private final TempStockService stockService;

  // 임시
  private final StockRepository stockRepository;

  public List<PfStock> getStockList(Long portfolioNo) {
    List<PfStock> pfStockList = pfStockRepository.findByPortfolioId(portfolioNo);
    pfStockList.forEach(pfStock -> {
      Stock stock = pfStock.getStock();
      StockPriceResponse currentPrice = stockService.getStockPrice(stock.getCode());
      int currentPriceInt = Integer.parseInt(currentPrice.getOutput().getStck_prpr());
      pfStock.setCurrentPrice(currentPriceInt);
    });
    return pfStockList;
  }

  public PfStock savePfStock(Long portfolioNo, PfStockRequest pfStockRequest) {
    Portfolio portfolio = portfolioService.getPortfolio(portfolioNo);

    StockPriceResponse currentPrice = stockService.getStockPrice(pfStockRequest.getStockCode());
    int currentPriceInt = Integer.parseInt(currentPrice.getOutput().getStck_prpr());

    Stock stock = stockRepository.findByCode(pfStockRequest.getStockCode()).orElse(null);
    PfStock pfStock = PfStock.builder().portfolio(portfolio).stock(stock)
        .pfstockCount(pfStockRequest.getPfstockCount())
        .pfstockPrice(pfStockRequest.getPfstockPrice())
        .pfstockTotalPrice(pfStockRequest.getPfstockPrice() * pfStockRequest.getPfstockCount())
        .currentPrice(currentPriceInt).build();
    return pfStockRepository.save(pfStock);
  }

  public PfStock savePfStock(PfStock pfStock) {
    return pfStockRepository.save(pfStock);
  }

  public void deletePfStock(Long pfStockNo) {
    pfStockRepository.deleteById(pfStockNo);
  }

  public void buyPfStock(Long pfStockNo, PfStockPatchRequest pfStockPatchRequest) {
    PfStock pfStock = pfStockRepository.findById(pfStockNo).orElse(null);
    log.debug("pfStock : {}", pfStock);

    int quantity = pfStock.getPfstockCount();
    log.debug("quantity : {}", quantity);

    int totalPrice = pfStock.getPfstockPrice() * quantity;
    quantity += pfStockPatchRequest.getPfstockCount();
    log.debug("quantity : {}", quantity);

    totalPrice += pfStockPatchRequest.getPfstockPrice() * pfStockPatchRequest.getPfstockCount();

    pfStock.setPfstockCount(quantity);
    pfStock.setPfstockTotalPrice(totalPrice);

    pfStockRepository.save(pfStock);
  }

  public void sellPfStock(Long pfStockNo, PfStockPatchRequest pfStockPatchRequest) {
    PfStock pfStock = pfStockRepository.findById(pfStockNo).orElse(null);

    int quantity = pfStock.getPfstockCount();
    log.debug("quantity : {}", quantity);

    quantity -= pfStockPatchRequest.getPfstockCount();
    log.debug("quantity : {}", quantity);

    pfStock.setPfstockCount(quantity);

    /* 매도금액만큼 현금 추가하기 */

    pfStockRepository.save(pfStock);
  }


  public void update(Long pfStockNo, PfStockPatchRequest request) {
    PfStock pfStock = pfStockRepository.findById(pfStockNo).orElse(null);
    pfStock.setPfstockCount(request.getPfstockCount());
    pfStock.setPfstockPrice(request.getPfstockPrice());
    pfStock.setPfstockTotalPrice(request.getPfstockCount() * request.getPfstockPrice());
    pfStockRepository.save(pfStock);
  }

  // stock service로 이동 예정
  public List<Stock> searchStocks(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return Collections.emptyList();
    }

    String searchKeyword = keyword.toLowerCase();
    return stockRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(searchKeyword,
        searchKeyword);
  }

  // 임시 데이터
  public Stock saveTempStock(Stock stock) {
    return stockRepository.save(stock);
  }

  public Stock getTempStock(String n) {
    return stockRepository.findById(n).orElse(null);
  }

}
