package org.com.stocknote.domain.searchDoc.repository;

import org.com.stocknote.domain.searchDoc.document.PortfolioDoc;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PortfolioDocRepository extends ElasticsearchRepository<PortfolioDoc, String> {
  @Query("""
      {
        "match": {
          "member_id": ?0
        }
      }
  """)
  PortfolioDoc findByMemberId(Long memberId);
}
