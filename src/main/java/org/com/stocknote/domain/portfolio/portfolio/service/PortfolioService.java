package org.com.stocknote.domain.portfolio.portfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.domain.portfolio.portfolio.dto.request.PortfolioPatchRequest;
import org.com.stocknote.domain.portfolio.portfolio.dto.request.PortfolioRequest;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.repository.PortfolioRepository;
import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stockApi.dto.response.StockPriceResponse;
import org.com.stocknote.domain.stockApi.service.StockApiService;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {
  private final PortfolioRepository portfolioRepository;
  private final MemberRepository memberRepository;
  private final StockApiService stockApiService;


  public List<Portfolio> getPortfolioList(String email) {
    Long memberId = memberRepository.findByEmail(email).orElseThrow().getId();
    return portfolioRepository.findPortfoliosWithStocks(memberId);
  }

  public Portfolio getMyPortfolioList(String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    List<Portfolio> portfolioList = portfolioRepository.findByMember(member);

    List<PfStock> allPfStocks =
        portfolioList.stream().flatMap(p -> p.getPfStockList().stream()).map(pfStock -> {
          PfStock newPfStock = PfStock.builder().pfstockCount(pfStock.getPfstockCount())
              .pfstockPrice(pfStock.getPfstockPrice())
              .pfstockTotalPrice(pfStock.getPfstockTotalPrice())
              .currentPrice(pfStock.getCurrentPrice())
              .idxBztpSclsCdName(pfStock.getIdxBztpSclsCdName()).stock(pfStock.getStock())
              .id(pfStock.getId()).createdAt(pfStock.getCreatedAt())
              .modifiedAt(pfStock.getModifiedAt()).build();
          return newPfStock;
        }).collect(Collectors.toList());

    Portfolio totalPortfolio = Portfolio.builder().name("전체 포트폴리오").description("전체 포트폴리오")
        .totalAsset(portfolioList.stream().mapToInt(Portfolio::getTotalAsset).sum())
        .cash(portfolioList.stream().mapToInt(Portfolio::getCash).sum())
        .totalProfit(portfolioList.stream().mapToInt(Portfolio::getTotalProfit).sum())
        .totalStock(portfolioList.stream().mapToInt(Portfolio::getTotalStock).sum())
        .pfStockList(new ArrayList<>()) // pfStockList 초기화 추가
        .member(member).build();

    allPfStocks.forEach(totalPortfolio::addPfStock);

    return totalPortfolio;
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
      StockPriceResponse currentPrice = stockApiService.getStockPrice(stock.getCode()).block();
      int currentPriceInt = Integer.parseInt(currentPrice.getOutput().getStck_prpr());
      pfStock.setCurrentPrice(currentPriceInt);

      int stockProfit = (currentPriceInt - pfStock.getPfstockPrice()) * pfStock.getPfstockCount();
      portfolio.setTotalProfit(portfolio.getTotalProfit() + stockProfit);
      portfolio.setTotalStock(
          portfolio.getTotalStock() + pfStock.getPfstockCount() * pfStock.getPfstockPrice());
    });

    portfolio.setTotalAsset(
        portfolio.getTotalProfit() + portfolio.getTotalStock() + portfolio.getCash());

    portfolioRepository.save(portfolio);
    return portfolio;
  }

  @Transactional
  public void save(PortfolioRequest portfolioRequest, String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

    Portfolio portfolio = Portfolio.builder().name(portfolioRequest.getName())
        .description(portfolioRequest.getDescription()).member(member).build();

    portfolioRepository.save(portfolio);
  }

  // test
  @Transactional
  public void update(Long portfoliNo, PortfolioPatchRequest portfolioPatchRequest) {
    Portfolio portfolio = portfolioRepository.findById(portfoliNo).orElse(null);
    // 여기서 실패하면 프론트에 실패했다는 코드를 띄워줘야함
    if (portfolio == null) {
      log.error("Portfolio not found");
      return;
    }
    portfolio.setName(portfolioPatchRequest.getName().orElse(portfolio.getName()));
    portfolio
        .setDescription(portfolioPatchRequest.getDescription().orElse(portfolio.getDescription()));

  }

  @Transactional
  public void delete(Long portfolioNo) {
    Portfolio portfolio = portfolioRepository.findById(portfolioNo)
        .orElseThrow(() -> new RuntimeException("Portfolio not found"));
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
    portfolio.setTotalAsset(amount + portfolio.getTotalProfit() + portfolio.getTotalStock());

    portfolioRepository.save(portfolio);
  }

  @Transactional
  public void deleteCash(Long portfolioNo) {
    Portfolio portfolio = portfolioRepository.findById(portfolioNo)
        .orElseThrow(() -> new RuntimeException("Portfolio not found"));
    portfolio.setCash(0);
    portfolio.setTotalAsset(portfolio.getTotalProfit() + portfolio.getTotalStock());

    portfolioRepository.save(portfolio);
  }

}
