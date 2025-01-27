package org.com.stocknote.domain.comment.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.comment.dto.CommentDetailResponse;
import org.com.stocknote.domain.comment.dto.CommentRequest;
import org.com.stocknote.domain.comment.dto.CommentUpdateDto;
import org.com.stocknote.domain.comment.service.CommentService;
import org.com.stocknote.global.dto.GlobalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @GetMapping
    public GlobalResponse<Page<CommentDetailResponse>> getComments(@PathVariable(value = "postId") Long postId, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return GlobalResponse.success(commentService.getComments(postId, pageable));
    }

    @GetMapping("/{commentId}")
    public GlobalResponse<CommentDetailResponse> getComment(@PathVariable(value = "commentId") Long commentId) {
        return GlobalResponse.success(commentService.getCommentDetail(commentId));
    }

    @PostMapping
    public GlobalResponse<Long> createComment(@PathVariable(value = "postId") Long postId, @RequestBody CommentRequest commentRequest, Authentication authentication) {

        String userEmail = authentication.getPrincipal().toString();
        return GlobalResponse.success(commentService.createComment(postId, commentRequest, userEmail));
    }

    @PatchMapping("/{commentId}")
    public GlobalResponse<Void> updateComment(@PathVariable(value = "postId") Long postId, @PathVariable(value = "commentId") Long commentId, @RequestBody CommentRequest commentRequest, Authentication authentication) {
        String userEmail = authentication.getPrincipal().toString();

        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(postId, commentId, commentRequest.getBody(), userEmail);

        commentService.updateComment(commentUpdateDto);

        return GlobalResponse.success();
    }

    @DeleteMapping("/{commentId}")
    public GlobalResponse<Void> deleteComment(@PathVariable(value = "commentId") Long commentId, Authentication authentication) {
        String userEmail = authentication.getPrincipal().toString();
        commentService.deleteComment(commentId, userEmail);

        return GlobalResponse.success();
    }
}