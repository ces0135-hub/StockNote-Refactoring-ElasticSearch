package org.com.stocknote.domain.portfolio.portfolio.dto.request;


import lombok.Data;

import java.util.Optional;

@Data
public class PortfolioPatchRequest {
  private Optional<String> name;
  private Optional<String> description;
}
