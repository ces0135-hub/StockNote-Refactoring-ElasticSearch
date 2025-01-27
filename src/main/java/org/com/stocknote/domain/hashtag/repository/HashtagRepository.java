package org.com.stocknote.domain.hashtag.repository;

import org.com.stocknote.domain.hashtag.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findByPostId(Long postId);

    void deleteByPostId(Long postId);
}
