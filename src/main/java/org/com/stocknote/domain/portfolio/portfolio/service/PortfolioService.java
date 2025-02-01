package org.com.stocknote.domain.portfolio.portfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.portfolio.portfolio.dto.request.PortfolioPatchRequest;
import org.com.stocknote.domain.portfolio.portfolio.dto.request.PortfolioRequest;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.repository.PortfolioRepository;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.portfolio.portfolioStock.repository.PfStockRepository;
import org.com.stocknote.domain.portfolio.portfolioStock.service.TempStockService;
import org.com.stocknote.domain.stock.dto.response.StockPriceResponse;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {
  private final PortfolioRepository portfolioRepository;
  private final SecurityUtils securityUtils;
  private final TempStockService stockService;


  public List<Portfolio> getPortfolioList() {
    Member member = securityUtils.getCurrentMember();
    return portfolioRepository.findByMember(member);
  }

  @Transactional
  public Portfolio getPortfolio(Long portfolioNo) {
    Portfolio portfolio = portfolioRepository.findById(portfolioNo)
        .orElseThrow(() -> new RuntimeException("Portfolio not found"));

    portfolio.setTotalProfit(0);
    portfolio.setTotalStock(0);

    List<PfStock> pfStockList = portfolio.getPfStockList();
    pfStockList.forEach(pfStock -> {
      Stock stock = pfStock.getStock();
      StockPriceResponse currentPrice = stockService.getStockPrice(stock.getCode());
      int currentPriceInt = Integer.parseInt(currentPrice.getOutput().getStck_prpr());
      pfStock.setCurrentPrice(currentPriceInt);

      int stockProfit = (currentPriceInt - pfStock.getPfstockPrice()) * pfStock.getPfstockCount();
      portfolio.setTotalProfit(portfolio.getTotalProfit() + stockProfit);
      portfolio.setTotalStock(portfolio.getTotalStock() + pfStock.getPfstockCount()*pfStock.getPfstockPrice());
    });

    portfolio.setTotalAsset(portfolio.getTotalProfit()+portfolio.getTotalStock()+portfolio.getCash());

    portfolioRepository.save(portfolio);
    return portfolio;
  }

  @Transactional
  public void save(PortfolioRequest portfolioRequest) {
    Member member = securityUtils.getCurrentMember();
    Portfolio portfolio = Portfolio.builder()
        .name(portfolioRequest.getName())
        .description(portfolioRequest.getDescription())
        .member(member)
        .build();
    portfolioRepository.save(portfolio);
  }
//test
  @Transactional
  public void update(Long portfoliNo, PortfolioPatchRequest portfolioPatchRequest) {
    Portfolio portfolio = portfolioRepository.findById(portfoliNo)
        .orElse(null);
    // 여기서 실패하면 프론트에 실패했다는 코드를 띄워줘야함
    if (portfolio == null) {
        log.error("Portfolio not found");
        return;
    }
    portfolio.setName(portfolioPatchRequest.getName().orElse(portfolio.getName()));
    portfolio.setDescription(portfolioPatchRequest.getDescription().orElse(portfolio.getDescription()));
  }

  @Transactional
  public void delete(Long portfolioNo) {
    portfolioRepository.deleteById(portfolioNo);
  }

  @Transactional
  public void addCash(Long portfolioNo, Integer amount) {
    Portfolio portfolio = portfolioRepository.findById(portfolioNo)
        .orElseThrow(() -> new RuntimeException("Portfolio not found"));
    portfolio.setCash(portfolio.getCash() + amount);
    portfolio.setTotalAsset(portfolio.getTotalAsset() + amount);

    portfolioRepository.save(portfolio);
  }

  @Transactional
  public void updateCash(Long portfolioNo, Integer amount) {
    Portfolio portfolio = portfolioRepository.findById(portfolioNo)
        .orElseThrow(() -> new RuntimeException("Portfolio not found"));
    portfolio.setCash(amount);
    portfolio.setTotalAsset(amount+portfolio.getTotalProfit()+portfolio.getTotalStock());

    portfolioRepository.save(portfolio);
  }

  @Transactional
  public void deleteCash(Long portfolioNo) {
    Portfolio portfolio = portfolioRepository.findById(portfolioNo)
        .orElseThrow(() -> new RuntimeException("Portfolio not found"));
    portfolio.setCash(0);
    portfolio.setTotalAsset(portfolio.getTotalProfit()+portfolio.getTotalStock());

    portfolioRepository.save(portfolio);
  }
}
