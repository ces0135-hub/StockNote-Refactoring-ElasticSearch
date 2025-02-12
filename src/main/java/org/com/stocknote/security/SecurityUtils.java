package org.com.stocknote.security;

import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
  private final MemberRepository memberRepository;

  public SecurityUtils(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  public String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new CustomException(ErrorCode.ACCESS_DENIED);
    }
    return authentication.getName();
  }

  public Member getCurrentMember() {
    String email = getCurrentUserEmail();
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
  }
}
