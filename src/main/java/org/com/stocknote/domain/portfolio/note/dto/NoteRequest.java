package org.com.stocknote.domain.portfolio.note.dto;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.portfolio.portfolio.entity.Portfolio;

@Data
public class NoteRequest {
  private String title;
  private String content;
  private Portfolio portfolio;
  private Member member;
}
