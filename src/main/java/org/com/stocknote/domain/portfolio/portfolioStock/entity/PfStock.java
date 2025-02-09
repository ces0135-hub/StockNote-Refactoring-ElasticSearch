package org.com.stocknote.domain.portfolio.portfolioStock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.global.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PfStock extends BaseEntity {
  private int pfstockCount;
  private int pfstockPrice;
  private int pfstockTotalPrice;
  private int currentPrice;
  private String idxBztpSclsCdName; //종목소분류

  @Column(nullable = true)
  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  private Portfolio portfolio;

  // portfolio setter 수정
  public void setPortfolio(Portfolio portfolio) {
    // 기존 portfolio에서 제거
    if (this.portfolio != null) {
      this.portfolio.getPfStockList().remove(this);
    }
    this.portfolio = portfolio;
    // 새로운 portfolio에 추가
    if (portfolio != null && !portfolio.getPfStockList().contains(this)) {
      portfolio.getPfStockList().add(this);
    }
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stock_code") // stock_code를 외래 키로 사용
  private Stock stock;


}
