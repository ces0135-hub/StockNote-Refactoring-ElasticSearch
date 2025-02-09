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
            "/community/articles",
            "/portfolio",
            "/interests"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String currentPath = request.getRequestURI();
        String redirectUri = request.getParameter("redirect_uri");

        logger.info("Current Path: {}", currentPath);
        logger.info("Redirect URI Parameter: {}", redirectUri);

        if (redirectUri != null && isAllowedRedirectPath(redirectUri)) {
            HttpSession session = request.getSession();
            session.setAttribute("REDIRECT_URI", redirectUri);
            logger.info("Saved redirect_uri parameter to session: {}", redirectUri);
        }
        else if (isAllowedRedirectPath(currentPath)) {
            HttpSession session = request.getSession();
            session.setAttribute("REDIRECT_URI", currentPath);
            logger.info("Saved current path to session: {}", currentPath);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowedRedirectPath(String path) {
        boolean isAllowed = ALLOWED_REDIRECT_PATHS.stream()
                .anyMatch(allowedPath -> path.startsWith(allowedPath));
        logger.info("Path: {} is allowed: {}", path, isAllowed);
        return isAllowed;
    }
}