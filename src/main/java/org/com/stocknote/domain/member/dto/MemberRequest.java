package org.com.stocknote.domain.member.dto;

import lombok.Data;

public class MemberRequest {
    @Data
    public static class LoginUserDto {
        private String email;
    }
}
