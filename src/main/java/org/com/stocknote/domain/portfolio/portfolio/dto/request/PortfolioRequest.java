package org.com.stocknote.domain.portfolio.portfolio.dto.request;

import jakarta.validation.Valid;
import lombok.Getter;

@Getter
public class PortfolioRequest {
  @Valid
  private String name;

  @Valid
  private String description;
}
