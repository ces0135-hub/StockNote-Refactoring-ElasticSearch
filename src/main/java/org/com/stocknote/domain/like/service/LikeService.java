package org.com.stocknote.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.like.entity.Like;
import org.com.stocknote.domain.like.repository.LikeRepository;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.com.stocknote.global.cache.service.CacheService;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CacheService cacheService;

    @Transactional
    public void likePost(Long postId, Member member) {
        if (likeRepository.existsByMemberIdAndPostId(member.getId(), postId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Like like = Like.builder()
                .member(member)
                .post(post)
                .build();

        likeRepository.save(like);
        cacheService.clearPopularPostsCache();
    }

    @Transactional
    public void unlikePost(Long postId, Member member) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Like like = likeRepository.findByMemberIdAndPostId(member.getId(), postId)
                .orElseThrow(() -> new IllegalStateException("User has not liked this post."));

        likeRepository.delete(like);
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public boolean hasUserLikedPost(Long postId, Member member) {
        return likeRepository.findByMemberIdAndPostId(member.getId(), postId).isPresent();
    }
}
