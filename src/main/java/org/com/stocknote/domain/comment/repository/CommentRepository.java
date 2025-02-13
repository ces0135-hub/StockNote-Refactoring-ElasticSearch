package org.com.stocknote.domain.comment.repository;

import org.com.stocknote.domain.comment.entity.Comment;
import org.com.stocknote.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByMember(Member member, Pageable pageable);
    boolean existsByPostIdAndMember(Long postId, Member member);
    @Query(value = "SELECT DISTINCT c FROM Comment c " +
            "JOIN FETCH c.member " +
            "WHERE c.post.id = :postId",
            countQuery = "SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    Page<Comment> findByPostId(@Param("postId") Long postId, Pageable pageable);
}
