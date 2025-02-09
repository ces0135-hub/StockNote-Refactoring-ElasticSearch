package org.com.stocknote.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SaveRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // API 요청이거나 허용된 경로인 경우 건너뛰기
        if (!isApiRequest(request) && !isPermittedPath(request)) {
            HttpSession session = request.getSession();
            String requestUri = request.getRequestURI();
            session.setAttribute("REDIRECT_URI", requestUri);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/");
    }

    private boolean isPermittedPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/") ||
                path.equals("/") ||
                path.startsWith("/oauth2/");
    }
}