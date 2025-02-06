package org.com.stocknote.domain.stockDoc.repository;

import org.com.stocknote.domain.stock.entity.Stock;
import org.com.stocknote.domain.stockDoc.document.StockDoc;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface StockDocRepository extends ElasticsearchRepository<StockDoc, String> {
  @Query("""
        {
            "bool": {
                "should": [
                    {
                        "match": {
                            "code": "?0"
                        }
                    },
                    {
                        "match": {
                            "name": "?0"
                        }
                    }
                ]
            }
        }
    """)
  List<Stock> searchByKeyword(String keyword);
}
