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
import java.util.Arrays;
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

            if (currentPath.contains("/oauth2/authorization")) {
                String redirectUri = request.getParameter("redirect_uri");
                logger.debug("OAuth2 authorization request detected. Redirect URI: {}", redirectUri);

                if (redirectUri != null && !redirectUri.isEmpty() && isAllowedRedirectPath(redirectUri)) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("REDIRECT_URI", redirectUri);
                    logger.debug("Saved redirect URI to session: {}", redirectUri);
                }
            }
        } catch (Exception e) {
            logger.error("Error in SaveRequestFilter", e);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowedRedirectPath(String path) {
        boolean isAllowed = ALLOWED_REDIRECT_PATHS.stream()
                .anyMatch(allowedPath -> path.startsWith(allowedPath));
        logger.debug("Checking path: {} - Allowed: {}", path, isAllowed);
        return isAllowed;
    }
}
