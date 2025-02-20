package org.com.stocknote.domain.searchDoc.repository;

import org.com.stocknote.domain.searchDoc.document.StockDoc;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
@ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true")
public interface StockDocRepository extends ElasticsearchRepository<StockDoc, String> {
  @Query("""
        {
            "bool": {
                "should": [
                   {
                       "wildcard": {
                           "code": "*?0*"
                       }
                   },
                   {
                       "wildcard": {
                           "name": "*?0*"
                       }
                   }
                ]
            }
        }
    """)
  List<StockDoc> searchByKeyword(String keyword);
}
