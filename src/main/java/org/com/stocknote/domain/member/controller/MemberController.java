package org.com.stocknote.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.keyword.dto.KeywordRequest;
import org.com.stocknote.domain.keyword.dto.KeywordResponse;
import org.com.stocknote.domain.member.dto.*;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.service.MemberService;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.com.stocknote.global.globalDto.GlobalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = " 회원 API", description = "User")
public class MemberController {
    private final MemberService memberService;


    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "회원 프로필 조회")
    public GlobalResponse<MemberDto> getUserProfile(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Member member = principalDetails.user();

        return GlobalResponse.success(MemberDto.of(member));
    }

    @PatchMapping("/profile/name")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "회원 이름 변경")
    public GlobalResponse<MemberDto> changeUserProfile(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody ChangeNameRequest request

    ) {
        System.out.println("request = " + request);
        Member member = principalDetails.user();
        Member updatedMember = memberService.updateProfile(member.getId(), request);

        return GlobalResponse.success(MemberDto.of(updatedMember));
    }

    @GetMapping("/comments")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내가 작성한 댓글 목록 조회")
    public org.com.stocknote.global.dto.GlobalResponse<Page<MyCommentResponse>> getMyComments(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            Pageable pageable
    ) {
        Member member = principalDetails.user();
        return org.com.stocknote.global.dto.GlobalResponse.success(
                memberService.findCommentsByMember(member, pageable)
        );
    }

    @GetMapping("/posts")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내가 작성한 게시글 목록 조회")
    public org.com.stocknote.global.dto.GlobalResponse<Page<MyPostResponse>> getMyPosts(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Member member = principalDetails.user();
        return org.com.stocknote.global.dto.GlobalResponse.success(
                memberService.findPostsByMember(member, pageable));
    }

}
