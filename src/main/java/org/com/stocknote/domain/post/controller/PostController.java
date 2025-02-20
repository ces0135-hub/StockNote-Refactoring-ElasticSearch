package org.com.stocknote.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.notification.service.KeywordNotificationElasticService;
import org.com.stocknote.domain.post.dto.PostModifyDto;
import org.com.stocknote.domain.post.dto.PostResponseDto;
import org.com.stocknote.domain.post.dto.PostSearchConditionDto;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.post.service.PostService;
import org.com.stocknote.domain.searchDoc.document.PostDoc;
import org.com.stocknote.domain.searchDoc.service.SearchDocService;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 게시글 API", description = "게시글(Post)")
@ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true")
public class PostController {

    private final PostService postService;
    private final SearchDocService searchDocService;
    private final KeywordNotificationElasticService keywordNotificationElasticService;
  
    @Transactional
    @PostMapping
    @Operation(summary = "게시글 작성")
    public GlobalResponse<Long> createPost(
            @Valid @RequestBody PostResponseDto postResponseDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Member member = principalDetails.user();
        Post post = postService.createPost(postResponseDto, member);
        PostDoc postDoc= searchDocService.transformPostDoc(post);
        keywordNotificationElasticService.createKeywordNotification(postDoc);
        return GlobalResponse.success(post.getId());
    }

    @GetMapping
    @Operation(summary = "게시글 목록 조회")
    public GlobalResponse<Page<PostResponseDto>> getPosts(
            @RequestParam(required = false, name= "category") PostCategory category,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (category == PostCategory.ALL) {
            return GlobalResponse.success(postService.getPosts(pageable));
        }
        return GlobalResponse.success(postService.getPostsByCategory(category, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회")
    public GlobalResponse<PostResponseDto> getPostById(@PathVariable("id") Long id) {
        return GlobalResponse.success(postService.getPostById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정")
    public GlobalResponse<String> updatePost(
            @PathVariable("id") Long id,
            @Valid @RequestBody PostModifyDto postModifyDto
    ) {
        postService.updatePost(id, postModifyDto);
        return GlobalResponse.success("Post updated successfully");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제")
    public GlobalResponse<String> deletePost(@PathVariable("id") Long id) {
        postService.deletePost(id);
        return GlobalResponse.success("Post deleted successfully");
    }

    @GetMapping("/popular")
    @Operation(summary = "인기글 조회")
    public GlobalResponse<Page<PostResponseDto>> getPopularPosts(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return GlobalResponse.success(postService.getPopularPosts(pageable));
    }

    @GetMapping("/popular/likes")
    @Operation(summary = "좋아요 순 인기글 조회")
    public GlobalResponse<Page<PostResponseDto>> getPopularPostsByLikes(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return GlobalResponse.success(postService.getPopularPostsByLikes(pageable));
    }

    @GetMapping("/popular/comments")
    @Operation(summary = "댓글 순 인기글 조회")
    public GlobalResponse<Page<PostResponseDto>> getPopularPostsByComments(
            @PageableDefault(size = 5) Pageable pageable
    ) {
        return GlobalResponse.success(postService.getPopularPostsByComments(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "게시글 검색")
    public GlobalResponse<Page<PostResponseDto>> searchPosts(
            @ModelAttribute PostSearchConditionDto condition,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return GlobalResponse.success(postService.searchPosts(condition, pageable));
    }

}
