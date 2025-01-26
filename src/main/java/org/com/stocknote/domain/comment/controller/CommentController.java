package org.com.stocknote.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.comment.dto.CommentRequest;
import org.com.stocknote.domain.comment.service.CommentService;
import org.com.stocknote.global.dto.GlobalResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public GlobalResponse<Long> createComment(@PathVariable(value = "postId") Long postId, @Valid @RequestBody CommentRequest commentRequest, Authentication authentication) {

        String userEmail = authentication.getPrincipal().toString();
        return GlobalResponse.success(commentService.createComment(postId, commentRequest, userEmail));
    }
}