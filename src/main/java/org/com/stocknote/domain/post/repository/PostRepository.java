package org.com.stocknote.domain.post.repository;

import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByCategory(PostCategory category, Pageable pageable);

    Long id(Long id);

    Page<Post> findByMember(Member member, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL ORDER BY size(p.likes) DESC, size(p.comments) DESC")
    Page<Post> findPopularPosts(Pageable pageable);
}