package org.com.stocknote.oauth.token.controller;

import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.entity.Role;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.com.stocknote.oauth.token.TokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/token")
public class TokenController {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public TokenController(TokenProvider tokenProvider, MemberRepository memberRepository) {
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/admin-token")
    public String generateAdminToken(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Member member = principalDetails.user();

        // 강제로 ADMIN 권한 부여
        member.setRole(Role.ADMIN);
        memberRepository.save(member);

        // 새로운 ADMIN 토큰 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new PrincipalDetails(member, principalDetails.getAttributes(), "memberId"),
                null,
                Collections.singleton(new SimpleGrantedAuthority(Role.ADMIN.name()))
        );

        String adminToken = tokenProvider.generateAccessToken(authentication);
        return adminToken;
    }
}
