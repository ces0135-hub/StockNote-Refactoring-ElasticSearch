package org.com.stocknote.domain.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.comment.dto.CommentDetailResponse;
import org.com.stocknote.domain.comment.dto.CommentRequest;
import org.com.stocknote.domain.comment.dto.CommentUpdateDto;
import org.com.stocknote.domain.comment.service.CommentService;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.global.dto.GlobalResponse;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "게시판 댓글 API", description = "댓글(comment)")
public class CommentController {

    private final CommentService commentService;



//    @GetMapping
//    public GlobalResponse<Page<CommentDetailResponse>> getComments(@PathVariable(value = "postId") Long postId, Pageable pageable) {
//        return GlobalResponse.success(commentService.getComments(postId, pageable));
//    }


    @GetMapping("/{commentId}")
    @Operation(summary = "댓글 상세 조회")
    public GlobalResponse<CommentDetailResponse> getComment(@PathVariable(value = "commentId") Long commentId) {
        return GlobalResponse.success(commentService.getCommentDetail(commentId));
    }

    @PostMapping
    @Operation(summary = "댓글 작성")
    public GlobalResponse<Long> createComment(@PathVariable(value = "postId") Long postId,
                                              @RequestBody CommentRequest commentRequest,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        Member member = principalDetails.user();
        return GlobalResponse.success(commentService.createComment(postId, commentRequest, member));
    }
  
    @Operation(summary = "댓글 수정")
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
    @Operation(summary = "댓글 삭제")
    public GlobalResponse<Void> deleteComment(@PathVariable(value = "commentId") Long commentId,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Member member = principalDetails.user();
        commentService.deleteComment(commentId, member);
        return GlobalResponse.success();
    }

}