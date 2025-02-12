package org.com.stocknote.domain.like.repository;

import org.com.stocknote.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByMemberIdAndPostId(Long memberId, Long postId);

    Long countByPostId(Long postId);

    void deleteByPostId(Long postId);

    Long postId(Long postId);

    @Query("SELECT EXISTS (SELECT 1 FROM Like l WHERE l.member.id = :memberId AND l.post.id = :postId)")
    boolean existsByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);


}
