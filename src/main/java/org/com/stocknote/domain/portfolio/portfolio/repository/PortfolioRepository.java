package org.com.stocknote.domain.portfolio.portfolio.repository;

import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
  List<Portfolio> findByMember(Member member);

  @Query("SELECT DISTINCT p FROM Portfolio p " +
          "LEFT JOIN FETCH p.pfStockList ps " +
          "LEFT JOIN FETCH ps.stock " +
          "WHERE p.member.id = :memberId")
  List<Portfolio> findPortfoliosWithStocks(@Param("memberId") Long memberId);



}
