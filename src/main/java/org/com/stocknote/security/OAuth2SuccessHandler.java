package org.com.stocknote.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.com.stocknote.config.AppConfig;
import org.com.stocknote.oauth.token.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    private final TokenProvider tokenProvider;
    private static final String URI = AppConfig.getSiteFrontUrl();
    private static final String DEFAULT_SUCCESS_URL = "/";

    private static final List<String> ALLOWED_REDIRECT_PATHS = Arrays.asList(
            "/portfolio",
            "/portfolio/total",
            "/community/articles",
            "/stocks",
            "/board"
    );

    public OAuth2SuccessHandler(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Enhanced logging for debugging
        logger.debug("Full Request URI: {}", request.getRequestURI());
        logger.debug("Full Query String: {}", request.getQueryString());

        // Log all parameters
        request.getParameterMap().forEach((key, values) ->
                logger.debug("Parameter - {}: {}", key, Arrays.toString(values))
        );

        // Log session attributes
        if (request.getSession() != null) {
            Enumeration<String> attributeNames = request.getSession().getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                logger.debug("Session Attribute - {}: {}",
                        attributeName,
                        request.getSession().getAttribute(attributeName)
                );
            }
        }

        String accessToken = handleAuthentication(authentication);
        String targetUrl = determineTargetUrl(request);
        logger.info("Authentication success. Target URL: {}", targetUrl);

        String redirectUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        logger.info("Redirecting to: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request) {
        String redirectUri = request.getParameter("redirect_uri");
        logger.info("Checking redirect_uri parameter: {}", redirectUri);

        // Exclude notification and SSE paths
        if (redirectUri != null && (
                redirectUri.contains("/notifications") ||
                        redirectUri.contains("/sse") ||
                        redirectUri.equals("/")
        )) {
            logger.info("Notification or SSE-related redirect, using default URL");
            return URI + DEFAULT_SUCCESS_URL;
        }

        // Check session for redirect URI
        if (redirectUri == null && request.getSession() != null) {
            redirectUri = (String) request.getSession().getAttribute("REDIRECT_URI");
            logger.info("Retrieved redirect URI from session: {}", redirectUri);

            // Remove URI from session after use
            request.getSession().removeAttribute("REDIRECT_URI");
        }

        // Validate redirect URI
        if (redirectUri != null && !redirectUri.isEmpty() && !"/".equals(redirectUri) && isAllowedRedirectPath(redirectUri)) {
            String fullRedirectUrl = URI + redirectUri;
            logger.info("Using redirect URL: {}", fullRedirectUrl);
            return fullRedirectUrl;
        }

        // Default to main page
        logger.warn("No valid redirect URI found. Using default success URL.");
        return URI + DEFAULT_SUCCESS_URL;
    }

    private boolean isAllowedRedirectPath(String path) {
        boolean isAllowed = ALLOWED_REDIRECT_PATHS.stream()
                .anyMatch(allowedPath -> path.startsWith(allowedPath));
        logger.info("Checking path: {} - Allowed: {}", path, isAllowed);
        return isAllowed;
    }

    private String handleAuthentication(Authentication authentication) {
        String accessToken = tokenProvider.generateAccessToken(authentication);
        tokenProvider.generateRefreshToken(authentication, accessToken);
        return accessToken;
    }
}