package org.com.stocknote.domain.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.post.dto.PostCreateDto;
import org.com.stocknote.domain.post.dto.PostModifyDto;
import org.com.stocknote.domain.post.dto.PostResponseDto;
import org.com.stocknote.domain.post.service.PostService;
import org.com.stocknote.global.dto.GlobalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public GlobalResponse<Long> createPost(
            @Valid @RequestBody PostCreateDto postCreateDto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return GlobalResponse.success(postService.createPost(postCreateDto, email));
    }

    @GetMapping
    public GlobalResponse<Page<PostResponseDto>> getAllPosts(Pageable pageable) {
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
}