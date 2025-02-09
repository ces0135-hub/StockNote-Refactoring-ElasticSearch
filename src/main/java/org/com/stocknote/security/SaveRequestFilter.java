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
import java.util.Enumeration;
import java.util.List;

@Component
public class SaveRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(SaveRequestFilter.class);

    private static final List<String> ALLOWED_REDIRECT_PATHS = Arrays.asList(
            "/portfolio",
            "/portfolio/total",
            "/community/articles",
            "/stocks",
            "/board"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String currentPath = request.getRequestURI();
            String queryString = request.getQueryString();

            logger.debug("Request URI: {}", currentPath);
            logger.debug("Query String: {}", queryString);

            // OAuth2 로그인 요청 확인 (확장된 조건)
            if (currentPath.contains("/oauth2/authorization") ||
                    (currentPath.contains("/auth/") && currentPath.contains("redirect"))) {

                // 세션에서 리다이렉트 URI 확인
                HttpSession session = request.getSession(false);
                String redirectUriFromSession = session != null ?
                        (String) session.getAttribute("REDIRECT_URI") : null;

                // 쿼리 파라미터에서 리다이렉트 URI 확인
                String redirectUriFromParam = request.getParameter("redirect_uri");

                // 우선순위: 쿼리 파라미터 > 세션
                String redirectUri = redirectUriFromParam != null ?
                        redirectUriFromParam : redirectUriFromSession;

                logger.debug("OAuth2 LOGIN - Detected potential redirect URI: {}", redirectUri);
                logger.debug("Session attributes: {}", getSessionAttributesAsString(request.getSession(false)));

                // 알림, SSE 관련 경로 제외
                boolean isNotificationPath = redirectUri != null && (
                        redirectUri.startsWith("/notifications/") ||
                                redirectUri.startsWith("/sse/")
                );

                // 커뮤니티 등 허용된 경로만 세션에 저장
                if (!isNotificationPath &&
                        redirectUri != null &&
                        !"/".equals(redirectUri) &&
                        isAllowedRedirectPath(redirectUri)) {

                    // 세션 생성 및 리다이렉트 URI 저장
                    if (session == null) {
                        session = request.getSession(true);
                    }
                    session.setAttribute("REDIRECT_URI", redirectUri);
                    logger.debug("Saved redirect URI to session: {}", redirectUri);
                }
            }
        } catch (Exception e) {
            logger.error("Error in SaveRequestFilter", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getSessionAttributesAsString(HttpSession session) {
        if (session == null) return "No session";

        StringBuilder sb = new StringBuilder();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            sb.append(attributeName).append(": ").append(session.getAttribute(attributeName)).append("; ");
        }
        return sb.toString();
    }

    private String extractRedirectUri(HttpServletRequest request) {
        // Extract from query parameters
        String redirectUriParam = request.getParameter("redirect_uri");
        if (redirectUriParam != null && !redirectUriParam.isEmpty()) {
            try {
                String decodedUri = URLDecoder.decode(redirectUriParam, StandardCharsets.UTF_8.toString());
                logger.debug("Extracted redirect_uri from query param: {}", decodedUri);
                return decodedUri;
            } catch (UnsupportedEncodingException e) {
                logger.error("Error decoding redirect_uri", e);
            }
        }

        // Extract from Referer header
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                URL url = new URL(referer);
                String path = url.getPath();

                if (path != null && !path.isEmpty() && !"/".equals(path)) {
                    logger.debug("Extracted redirect_uri from Referer: {}", path);
                    return path;
                }
            } catch (MalformedURLException e) {
                logger.error("Invalid Referer URL", e);
            }
        }

        // Check session for redirect URI
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object storedUri = session.getAttribute("REDIRECT_URI");
            if (storedUri != null) {
                logger.debug("Retrieved redirect_uri from session: {}", storedUri);
                return storedUri.toString();
            }
        }

        // Default to root
        logger.debug("No valid redirect URI found. Using default: /");
        return "/";
    }

    private boolean isAllowedRedirectPath(String path) {
        boolean isAllowed = ALLOWED_REDIRECT_PATHS.stream()
                .anyMatch(allowedPath -> path.startsWith(allowedPath));
        logger.debug("Checking path: {} - Allowed: {}", path, isAllowed);
        return isAllowed;
    }
}