package org.com.stocknote.domain.portfolio.note.dto;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.com.stocknote.domain.portfolio.note.entity.Note;
import org.com.stocknote.domain.stock.entity.Stock;

@Setter
@Builder
public class NoteResponse {
  private String type;
  private int amount;
  private int price;

  public static NoteResponse from(Note note) {
    return NoteResponse.builder()
        .type(note.getType())
        .amount(note.getAmount())
        .price(note.getPrice())
        .build();
  }
}
