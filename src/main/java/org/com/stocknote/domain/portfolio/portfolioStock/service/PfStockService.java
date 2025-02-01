package org.com.stocknote.domain.portfolio.portfolioStock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.service.PortfolioService;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.PfStockPatchRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.dto.PfStockRequest;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.portfolio.portfolioStock.repository.PfStockRepository;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PfStockService {
  private final PfStockRepository pfStockRepository;
  private final PortfolioService portfolioService;

  //임시
  private final StockRepository stockRepository;

  public List<PfStock> getStockList(Long portfolioNo) {
    return pfStockRepository.findByPortfolioId(portfolioNo);
  }

  public PfStock savePfStock(Long portfolioNo, PfStockRequest pfStockRequest) {
    Portfolio portfolio = portfolioService.getPortfolio(portfolioNo);
    Stock stock = stockRepository.findByCode(pfStockRequest.getStockCode()).orElse(null);
    PfStock pfStock = PfStock.builder()
        .portfolio(portfolio)
        .stock(stock)
        .pfstockCount(pfStockRequest.getPfstockCount())
        .pfstockPrice(pfStockRequest.getPfstockPrice())
        .pfstockTotalPrice(pfStockRequest.getPfstockPrice()*pfStockRequest.getPfstockCount())
        .build();
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

    /* 매도금액만큼 현금 추가하기*/

    pfStockRepository.save(pfStock);
  }


  public void update(Long pfStockNo, PfStockPatchRequest request) {
    PfStock pfStock = pfStockRepository.findById(pfStockNo).orElse(null);
    pfStock.setPfstockCount(request.getPfstockCount());
    pfStock.setPfstockPrice(request.getPfstockPrice());
    pfStock.setPfstockTotalPrice(
        request.getPfstockCount() * request.getPfstockPrice()
    );
    pfStockRepository.save(pfStock);
  }





  //임시 데이터
  public Stock saveTempStock(Stock stock) {
    return stockRepository.save(stock);
  }

  public Stock getTempStock(String n) {
    return stockRepository.findById(n).orElse(null);
  }

  public List<Stock> getTempStockList() {
    return stockRepository.findAll();
  }
}
