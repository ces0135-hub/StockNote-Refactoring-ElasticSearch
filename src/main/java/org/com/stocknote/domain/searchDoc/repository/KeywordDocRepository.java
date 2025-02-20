package org.com.stocknote.domain.searchDoc.repository;

import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.searchDoc.document.KeywordDoc;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
@ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true")
public interface KeywordDocRepository extends ElasticsearchRepository<KeywordDoc, String> {
    @Query("""
    {
      "bool": {
        "must": [
          {
            "bool": {
              "should": [
                {"match": {"post_category": "#{#postCategory}"}},
                {"match": {"post_category": "ALL"}}
              ]
            }
          },
          {
            "bool": {
              "should": [
                {"match": {"keyword": "#{#title}"}},
                {"match": {"keyword": "#{#hashtags}"}}
              ]
            }
          }
        ]
      }
    }
    """)
    List<KeywordDoc> findMatchingKeywords(
            @Param("postCategory") PostCategory postCategory,
            @Param("title") String title,
            @Param("hashtags") String hashtags
    );
}
