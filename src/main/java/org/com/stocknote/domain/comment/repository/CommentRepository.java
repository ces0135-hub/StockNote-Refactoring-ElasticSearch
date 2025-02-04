package org.com.stocknote.domain.comment.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.com.stocknote.domain.comment.entity.Comment;
import org.com.stocknote.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostId(Pageable pageable, Long postId);
    Page<Comment> findByMember(Member member, Pageable pageable);

}
