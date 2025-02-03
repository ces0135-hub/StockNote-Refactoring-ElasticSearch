package org.com.stocknote.domain.comment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.comment.dto.CommentDetailResponse;
import org.com.stocknote.domain.comment.dto.CommentRequest;
import org.com.stocknote.domain.comment.dto.CommentUpdateDto;
import org.com.stocknote.domain.comment.dto.MyCommentResponse;
import org.com.stocknote.domain.comment.service.CommentService;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.post.dto.MyPostResponseDto;
import org.com.stocknote.global.dto.GlobalResponse;

import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


//    @GetMapping
//    public GlobalResponse<Page<CommentDetailResponse>> getComments(@PathVariable(value = "postId") Long postId, Pageable pageable) {
//        return GlobalResponse.success(commentService.getComments(postId, pageable));
//    }

    @GetMapping("/{commentId}")
    public GlobalResponse<CommentDetailResponse> getComment(@PathVariable(value = "commentId") Long commentId) {
        return GlobalResponse.success(commentService.getCommentDetail(commentId));
    }

    @PostMapping
    public GlobalResponse<Long> createComment(@PathVariable(value = "postId") Long postId,
                                              @RequestBody CommentRequest commentRequest,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        Member member = principalDetails.user();
        return GlobalResponse.success(commentService.createComment(postId, commentRequest, member));
    }

    @PatchMapping("/{commentId}")
    public GlobalResponse<Void> updateComment(@PathVariable(value = "postId") Long postId,
                                              @PathVariable(value = "commentId") Long commentId,
                                              @RequestBody CommentRequest commentRequest,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Member member = principalDetails.user();

        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(commentId, postId, commentRequest.getBody());

        commentService.updateComment(commentUpdateDto, member);

        return GlobalResponse.success();
    }

    @DeleteMapping("/{commentId}")
    public GlobalResponse<Void> deleteComment(@PathVariable(value = "commentId") Long commentId,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Member member = principalDetails.user();
        commentService.deleteComment(commentId, member);
        return GlobalResponse.success();
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Tag(name = "내가 쓴 글 조회 API", description = "사용자가 작성한 게시글 목록을 조회합니다.")
    public GlobalResponse<Page<MyCommentResponse>> getMyComments(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Pageable pageable
    ) {
        Member member = principalDetails.user();
        return GlobalResponse.success(
                commentService.findCommentsByMember(member, pageable)
        );
    }
}