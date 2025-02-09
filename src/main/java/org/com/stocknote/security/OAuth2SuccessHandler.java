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
            "/stocks"
    );

    public OAuth2SuccessHandler(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
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

        // 세션에서 리다이렉트 URI 확인
        if (redirectUri == null && request.getSession() != null) {
            redirectUri = (String) request.getSession().getAttribute("REDIRECT_URI");
            logger.info("Retrieved redirect URI from session: {}", redirectUri);
            request.getSession().removeAttribute("REDIRECT_URI");
        }

        // 리다이렉트 URI가 null이거나 비어있지 않고, 허용된 경로인지 확인
        if (redirectUri != null && !redirectUri.isEmpty() && isAllowedRedirectPath(redirectUri)) {
            return URI + redirectUri;
        }

        // 디버그 로그 추가
        logger.warn("No valid redirect URI found. Using default success URL.");
        return URI + DEFAULT_SUCCESS_URL;
    }

    private boolean isAllowedRedirectPath(String path) {
        return ALLOWED_REDIRECT_PATHS.stream()
                .anyMatch(allowedPath -> path.startsWith(allowedPath));
    }

    private String handleAuthentication(Authentication authentication) {
        String accessToken = tokenProvider.generateAccessToken(authentication);
        tokenProvider.generateRefreshToken(authentication, accessToken);
        return accessToken;
    }
}