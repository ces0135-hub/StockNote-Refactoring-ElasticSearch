package org.com.stocknote.domain.searchDoc.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.com.stocknote.domain.member.entity.Member;

@Document(indexName = "stocknote_member", createIndex = true)
@Getter
@Setter
@Builder
@NoArgsConstructor // 추가
@AllArgsConstructor // 추가
public class MemberDoc {
  @Id
  private String id;

  @Field(type = FieldType.Keyword)
  private String email;

  @Field(type = FieldType.Text)
  private String name;

  @Field(type = FieldType.Keyword)
  private String profile;

  @Field(type = FieldType.Keyword)
  private String provider;

  @Field(type = FieldType.Keyword, name = "provider_id")
  private String providerId;

  public static MemberDoc of(Member member){
    return MemberDoc.builder()
            .id(String.valueOf(member.getId()))
            .email(member.getEmail())
            .name(member.getName())
            .provider(String.valueOf(member.getProvider()))
            .providerId(member.getProviderId())
            .build();
  }
}
