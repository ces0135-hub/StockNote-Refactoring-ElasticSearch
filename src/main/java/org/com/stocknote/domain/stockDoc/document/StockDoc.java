package org.com.stocknote.domain.stockDoc.document;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Document(indexName = "stocknote_stock", createIndex = true)
@Getter
@Setter
@Builder
@NoArgsConstructor // 추가
@AllArgsConstructor // 추가
public class StockDoc {
  @Id
  private String code;

  @Field(type = FieldType.Text)
  private String name;

  @Field(type = FieldType.Text)
  private String market;
}
