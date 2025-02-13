package org.com.stocknote.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.error.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ErrorCode errorCode = ErrorCode.SC_FORBIDDEN;
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now().toString(),errorCode.getHttpStatus().name(), "error happendfadskl");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
