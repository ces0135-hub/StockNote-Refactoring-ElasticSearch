package org.com.stocknote.domain.portfolio.note.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.com.stocknote.domain.portfolio.note.entity.Note;
import org.com.stocknote.domain.stock.entity.Stock;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponse {
  private String type;
  private String stockName; // Stock 엔티티 대신 필요한 필드만 추출
  private String market; // Stock 엔티티 대신 필요한 필드만 추출
  private int amount;
  private int price;
  private String createdAt; // 거래 일자 추가

  public static NoteResponse from(Note note) {
    return NoteResponse.builder().type(note.getType()).stockName(note.getStock().getName())
        .market(note.getStock().getMarket()).amount(note.getAmount()).price(note.getPrice())
        .createdAt(note.getCreatedAt().toString()).build();
  }
}
