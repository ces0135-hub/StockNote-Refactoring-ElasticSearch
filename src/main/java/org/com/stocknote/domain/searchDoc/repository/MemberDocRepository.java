package org.com.stocknote.domain.searchDoc.repository;

import org.com.stocknote.domain.searchDoc.document.MemberDoc;
import org.com.stocknote.domain.searchDoc.document.StockDoc;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MemberDocRepository extends ElasticsearchRepository<MemberDoc, String> {
}
