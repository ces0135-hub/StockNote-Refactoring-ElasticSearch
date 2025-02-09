package org.com.stocknote.domain.searchDoc.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "stocknote_pfstock", createIndex = true)
@Getter
@Setter
@Builder
@NoArgsConstructor // 추가
@AllArgsConstructor // 추가
public class Test_PortfolioStockDoc {
  @Id
  private Long id;

  // PfStock 정보
  @Field(type = FieldType.Integer)
  private int pfstockCount;

  @Field(type = FieldType.Integer)
  private int pfstockPrice;

  @Field(type = FieldType.Integer)
  private int pfstockTotalPrice;

  @Field(type = FieldType.Integer)
  private int currentPrice;

  @Field(type = FieldType.Keyword)
  private String idxBztpSclsCdName;

  // Portfolio 합계 정보
  @Field(type = FieldType.Integer)
  private int totalAsset;

  @Field(type = FieldType.Integer)
  private int totalCash;

  @Field(type = FieldType.Integer)
  private int totalProfit;

  @Field(type = FieldType.Integer)
  private int totalStock;

  // 중첩된 Stock 정보
  @Field(type = FieldType.Nested)
  private StockDoc stockDoc;

  // 중첩된 Member 정보
  @Field(type = FieldType.Nested)
  private MemberDoc memberDoc;
}
