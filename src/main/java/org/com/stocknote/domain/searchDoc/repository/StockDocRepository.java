package org.com.stocknote.domain.searchDoc.repository;

import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.searchDoc.document.StockDoc;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

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
