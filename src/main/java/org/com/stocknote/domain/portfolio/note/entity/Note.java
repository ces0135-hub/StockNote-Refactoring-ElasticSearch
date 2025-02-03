package org.com.stocknote.domain.portfolio.note.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;
import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.global.base.BaseEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Note extends BaseEntity {
  private String type;

  @ManyToOne(fetch = FetchType.LAZY)
  private Stock stock;

  private int amount;
  private int price;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)  // 추가
  private Portfolio portfolio;

  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;
}
