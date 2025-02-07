package org.com.stocknote.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.hashtag.entity.Hashtag;
import org.com.stocknote.domain.hashtag.service.HashtagService;
import org.com.stocknote.domain.like.repository.LikeRepository;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.notification.repository.CommentNotificationRepository;
import org.com.stocknote.domain.post.dto.PostCreateDto;
import org.com.stocknote.domain.post.dto.PostModifyDto;
import org.com.stocknote.domain.post.dto.PostResponseDto;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.post.repository.PostRepository;
import org.com.stocknote.domain.post.repository.PostSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final HashtagService hashtagService;
    private final CommentNotificationRepository commentNotificationRepository;
    private final LikeRepository likeRepository;
    @Autowired
    private final PostSearchRepository postSearchRepository;


    @Transactional
    public Long createPost(PostCreateDto postCreateDto, Member member) {

        Post post = postCreateDto.toEntity(member);
        Post savedPost = postRepository.save(post);

        hashtagService.createHashtags(savedPost.getId(), postCreateDto.getHashtags());
        return savedPost.getId();
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> {
            List<String> hashtags = hashtagService.getHashtagsByPostId(post.getId())
                    .stream()
                    .map(Hashtag::getName)
                    .toList();
            return PostResponseDto.fromPost(post, hashtags);
        });
    }

    @Transactional(readOnly=true)
    public Page<PostResponseDto> getPostsByCategory(PostCategory category, Pageable pageable) {
        Page<Post> posts = postRepository.findByCategory(category, pageable);  // Pageable 추가
        return posts.map(post -> {
            List<String> hashtags = hashtagService.getHashtagsByPostId(post.getId())
                    .stream()
                    .map(Hashtag::getName)
                    .toList();

//            Long likeCount = likeRepository.countByPostId(post.getId());
            return PostResponseDto.fromPost(post, hashtags);

        });
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<String> hashtags = hashtagService.getHashtagsByPostId(post.getId())
                .stream()
                .map(Hashtag::getName)
                .toList();

//        Long likeCount = likeRepository.countByPostId(post.getId());
        return PostResponseDto.fromPost(post, hashtags);
    }

    @Transactional
    public void updatePost(Long id, PostModifyDto postModifyDto) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        existingPost.bodyUpdate(postModifyDto.getContent());
        existingPost.titleUpdate(postModifyDto.getTitle());
        existingPost.categoryUpdate(PostCategory.valueOf(postModifyDto.getCategory()));
        postRepository.save(existingPost);

        hashtagService.updateHashtags(id, postModifyDto.getHashtags());
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        likeRepository.deleteByPostId(id);
        hashtagService.deleteHashtagsByPostId(id);
        commentNotificationRepository.deleteByRelatedPostId(id);
        //댓글은 CASCADE로 삭제됨
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPopularPosts(Pageable pageable) {
        Page<Post> popularPosts = postRepository.findPopularPosts(pageable);

        List<PostResponseDto> sortedPosts = popularPosts.stream()
                .map(post -> {
                    List<String> hashtags = hashtagService.getHashtagsByPostId(post.getId())
                            .stream()
                            .map(Hashtag::getName)
                            .toList();
                    return PostResponseDto.fromPost(post, hashtags);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(sortedPosts, pageable, popularPosts.getTotalElements());
    }

    // 좋아요 순 조회
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPopularPostsByLikes(Pageable pageable) {
        Page<Post> popularPosts = postRepository.findPopularPostsByLikes(pageable);

        List<PostResponseDto> sortedPosts = popularPosts.stream()
                .map(post -> {
                    List<String> hashtags = hashtagService.getHashtagsByPostId(post.getId())
                            .stream()
                            .map(Hashtag::getName)
                            .toList();
                    return PostResponseDto.fromPost(post, hashtags);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(sortedPosts, pageable, popularPosts.getTotalElements());
    }

    // 댓글 순 조회
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPopularPostsByComments(Pageable pageable) {
        Page<Post> popularPosts = postRepository.findPopularPostsByComments(pageable);

        List<PostResponseDto> sortedPosts = popularPosts.stream()
                .map(post -> {
                    List<String> hashtags = hashtagService.getHashtagsByPostId(post.getId())
                            .stream()
                            .map(Hashtag::getName)
                            .toList();
                    return PostResponseDto.fromPost(post, hashtags);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(sortedPosts, pageable, popularPosts.getTotalElements());
    }

    // 검색 기능
    @Transactional(readOnly = true)
    public Page<PostResponseDto> searchPosts(PostSearchConditionDto condition, Pageable pageable) {
        // 검색 실행
        Page<Post> searchResults = postSearchRepository.search(condition, pageable);  // postRepositoryCustom -> postSearchRepository

        // PostResponseDto로 변환
        return searchResults.map(post -> {
            List<String> hashtags = hashtagService.getHashtagsByPostId(post.getId())
                    .stream()
                    .map(Hashtag::getName)
                    .toList();
            return PostResponseDto.fromPost(post, hashtags);
        });
    }

}
