package org.com.stocknote.domain.searchDoc.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "stocknote_portfolio", createIndex = true)
@Getter
@Setter
@Builder
@NoArgsConstructor // 추가
@AllArgsConstructor // 추가
public class PortfolioDoc {
  @Id
  private String id;
  // Portfolio 합계 정보
  @Field(type = FieldType.Integer, name = "total_asset")
  private int totalAsset;

  @Field(type = FieldType.Integer, name = "total_cash")
  private int totalCash;

  @Field(type = FieldType.Integer, name = "total_profit")
  private int totalProfit;

  @Field(type = FieldType.Integer, name = "total_stock")
  private int totalStock;

  // 중첩된 Member 정보
  @Field(name = "member_id")
  private Long memberId;
}
