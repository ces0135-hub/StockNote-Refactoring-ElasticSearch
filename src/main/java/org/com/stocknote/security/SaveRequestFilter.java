package org.com.stocknote.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class SaveRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(SaveRequestFilter.class);

    private static final List<String> ALLOWED_REDIRECT_PATHS = Arrays.asList(
            "/portfolio",
            "/portfolio/total",
            "/community/articles",
            "/stocks"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String currentPath = request.getRequestURI();
            String queryString = request.getQueryString();

            // OAuth2 로그인 관련 요청인지 확인
            if (currentPath.contains("/oauth2/authorization") ||
                    currentPath.contains("/auth/") && currentPath.contains("redirect")) {

                // 리다이렉트 URI 추출 시도
                String redirectUri = extractRedirectUri(request);

                logger.info("Detected potential redirect URI: {}", redirectUri);

                // 리다이렉트 URI 유효성 검사 및 세션 저장
                if (redirectUri != null && isAllowedRedirectPath(redirectUri)) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("REDIRECT_URI", redirectUri);
                    logger.info("Saved redirect URI to session: {}", redirectUri);
                }
            }
        } catch (Exception e) {
            logger.error("Error in SaveRequestFilter", e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractRedirectUri(HttpServletRequest request) {
        // 1. 쿼리 파라미터에서 redirect_uri 추출
        String redirectUriParam = request.getParameter("redirect_uri");
        if (redirectUriParam != null && !redirectUriParam.isEmpty()) {
            try {
                return URLDecoder.decode(redirectUriParam, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                logger.error("Error decoding redirect_uri", e);
            }
        }

        // 2. Referer 헤더에서 경로 추출
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                URL url = new URL(referer);
                String path = url.getPath();

                // 루트 경로가 아닌 경우에만 반환
                if (path != null && !path.isEmpty() && !"/".equals(path)) {
                    return path;
                }
            } catch (MalformedURLException e) {
                logger.error("Invalid Referer URL", e);
            }
        }

        // 3. 기본값 반환
        return "/";
    }

    private boolean isAllowedRedirectPath(String path) {
        boolean isAllowed = ALLOWED_REDIRECT_PATHS.stream()
                .anyMatch(allowedPath -> path.startsWith(allowedPath));
        logger.info("Checking path: {} - Allowed: {}", path, isAllowed);
        return isAllowed;
    }
}