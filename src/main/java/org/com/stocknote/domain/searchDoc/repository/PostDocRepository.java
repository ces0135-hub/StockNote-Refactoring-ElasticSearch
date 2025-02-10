package org.com.stocknote.domain.searchDoc.repository;

import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.searchDoc.document.PostDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PostDocRepository extends ElasticsearchRepository<PostDoc, String> {
  @Query("{\"match\": {\"title\": {\"query\": \"?0\"}}}")
  Page<PostDoc> searchByTitle(String keyword, Pageable pageable);

  @Query("{\"match\": {\"body\": {\"query\": \"?0\"}}}")
  Page<PostDoc> searchByContent(String keyword, Pageable pageable);

  @Query("{\"match\": {\"member_doc.name\": {\"query\": \"?0\"}}}")
  Page<PostDoc> searchByUsername(String keyword, Pageable pageable);

  @Query("{\"match\": {\"hashtags\": {\"query\": \"?0\"}}}")
  Page<PostDoc> searchByHashtag(String keyword, Pageable pageable);

  @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^3\", \"body\", \"member_doc.name^2\", \"hashtags^2\"]}}")
  Page<PostDoc> searchByAll(String keyword, Pageable pageable);

  @Query("{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"hashtags\": \"?0\"}}]}}")
  PostDoc searchByTitleOrHashtag(String keyword);

  @Query("{\"bool\": {\"should\": [" +
      "{\"match\": {\"title\": \"?0\"}}," +
      "{\"match\": {\"hashtags\": \"?0\"}}" +
      "]}}")
  boolean existsByTitleOrHashtagsContaining(String keyword);
}
