package org.com.stocknote.domain.searchDoc.document;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "stocknote_post", createIndex = true)
@Setting(settingPath = "/elasticsearch/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings.json")
@Getter
@Setter
@Builder
@NoArgsConstructor // 추가
@AllArgsConstructor // 추가
public class PostDoc {
  @Id
  private String id;

  @Field(type = FieldType.Text, name = "created_at")
  private String createdAt;

  @Field(type = FieldType.Text, name = "modified_at")
  private String modifiedAt;

  @Field(type = FieldType.Text)
  private String title;

  @Field(type = FieldType.Text)
  private String body;

  @Enumerated(EnumType.STRING)
  private PostCategory category;

  @Field(type = FieldType.Integer, name = "comment_count")
  private int commentCount;

  @Field(type = FieldType.Integer, name = "like_count")
  private int likeCount;

  @Field(type = FieldType.Text)
  private List<String> hashtags;

  @Field(type = FieldType.Nested, name = "member_doc")
  private MemberDoc memberDoc;
}
