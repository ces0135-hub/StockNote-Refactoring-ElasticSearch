package org.com.stocknote.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.error.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now().toString(),errorCode.getHttpStatus().name(), errorCode.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
