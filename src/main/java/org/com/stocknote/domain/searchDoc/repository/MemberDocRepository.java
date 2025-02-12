package org.com.stocknote.domain.searchDoc.repository;

import org.com.stocknote.domain.searchDoc.document.MemberDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface MemberDocRepository extends ElasticsearchRepository<MemberDoc, String> {
}
