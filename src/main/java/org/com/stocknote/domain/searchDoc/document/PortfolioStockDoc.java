package org.com.stocknote.domain.searchDoc.document;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.annotation.Id;

@Document(indexName = "stocknote_pfstock", createIndex = true)
@Getter
@Setter
@Builder
@NoArgsConstructor // 추가
@AllArgsConstructor // 추가
public class PortfolioStockDoc {
  @Id
  private String id;

  // PfStock 정보
  @Field(type = FieldType.Integer, name = "pfstock_count")
  private int pfstockCount;

  @Field(type = FieldType.Integer, name = "pfstock_price")
  private int pfstockPrice;

  @Field(type = FieldType.Integer, name = "pfstock_total_price")
  private int pfstockTotalPrice;

  @Field(type = FieldType.Integer, name = "current_price")
  private int currentPrice;

  @Field(type = FieldType.Keyword, name = "idx_bztp_scls_cd_name")
  private String idxBztpSclsCdName;

  // 중첩된 Stock 정보
  @Field(type = FieldType.Nested, name = "stock_doc")
  private StockDoc stockDoc;

  // 중첩된 Member 정보
  @Field(name = "member_id")
  private Long memberId;
}
