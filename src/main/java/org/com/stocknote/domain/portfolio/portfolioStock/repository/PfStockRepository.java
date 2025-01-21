package org.com.stocknote.domain.portfolio.portfolioStock.repository;

import org.com.stocknote.domain.portfolio.portfolioStock.entity.PfStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PfStockRepository extends JpaRepository<PfStock, Long> {
}
