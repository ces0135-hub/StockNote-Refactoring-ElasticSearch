package org.com.stocknote.domain.comment.repository;

import org.com.stocknote.domain.comment.entity.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostId(Pageable pageable, Long postId);

}
