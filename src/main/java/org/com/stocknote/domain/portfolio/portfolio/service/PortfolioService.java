package org.com.stocknote.domain.portfolio.portfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.stocknote.domain.portfolio.portfolio.dto.PortfolioRequest;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.portfolio.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;

  public void savePfList(Portfolio portfolio) {
    portfolioRepository.save(portfolio);

  }

  public List<Portfolio> getPortfolioList() {
    return portfolioRepository.findAll();
  }

  public void save(PortfolioRequest portfolioRequest) {
    Portfolio portfolio = Portfolio.builder()
        .name(portfolioRequest.getName())
        .description(portfolioRequest.getDescription())
        .build();
    savePfList(portfolio);
  }
}
