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
import org.com.stocknote.global.base.BaseEntity;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Note extends BaseEntity {
  private String title;
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  private Portfolio portfolio;

  @ManyToOne(fetch = FetchType.LAZY)
  private Member member;
}
