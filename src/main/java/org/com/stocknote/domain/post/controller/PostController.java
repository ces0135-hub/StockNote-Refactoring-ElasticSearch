package org.com.stocknote.domain.post.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.dto.MyPostResponseDto;
import org.com.stocknote.domain.post.dto.PostCreateDto;
import org.com.stocknote.domain.post.dto.PostModifyDto;
import org.com.stocknote.domain.post.dto.PostResponseDto;
import org.com.stocknote.domain.post.entity.Post;
import org.com.stocknote.domain.post.entity.PostCategory;
import org.com.stocknote.domain.post.service.PostService;
import org.com.stocknote.global.dto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public GlobalResponse<Long> createPost(
            @Valid @RequestBody PostCreateDto postCreateDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Member member = principalDetails.user();
        return GlobalResponse.success(postService.createPost(postCreateDto, member));
    }

//    @GetMapping
//    public GlobalResponse<Page<PostResponseDto>> getAllPosts(Pageable pageable) {
//        return GlobalResponse.success(postService.getPosts(pageable));
//    }

    @GetMapping
    public GlobalResponse<Page<PostResponseDto>> getPosts(
            @RequestParam(required = false, name= "category") PostCategory category,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (category != null) {
            return GlobalResponse.success(postService.getPostsByCategory(category, pageable));
        }
        return GlobalResponse.success(postService.getPosts(pageable));
    }

    @GetMapping("/{id}")
    public GlobalResponse<PostResponseDto> getPostById(@PathVariable("id") Long id) {
        return GlobalResponse.success(postService.getPostById(id));
    }

    @PutMapping("/{id}")
    public GlobalResponse<String> updatePost(
            @PathVariable("id") Long id,
            @Valid @RequestBody PostModifyDto postModifyDto
    ) {
        postService.updatePost(id, postModifyDto);
        return GlobalResponse.success("Post updated successfully");
    }

    @DeleteMapping("/{id}")
    public GlobalResponse<String> deletePost(@PathVariable("id") Long id) {
        postService.deletePost(id);
        return GlobalResponse.success("Post deleted successfully");
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Tag(name = "내가 쓴 글 조회 API", description = "사용자가 작성한 게시글 목록을 조회합니다.")
    public GlobalResponse<Page<MyPostResponseDto>> getMyPosts(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Member member = principalDetails.user();
        return GlobalResponse.success(
                postService.findPostsByMember(member, pageable));
    }
}