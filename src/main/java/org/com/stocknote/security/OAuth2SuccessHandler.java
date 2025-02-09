package org.com.stocknote.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.config.AppConfig;
import org.com.stocknote.oauth.token.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
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
            "/community/articles",
            "/portfolio",
            "/interests"
    );

    public OAuth2SuccessHandler(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        logger.info("Authentication success. Processing redirect...");
        String accessToken = handleAuthentication(authentication);
        String targetUrl = determineTargetUrl(request);
        logger.info("Target URL determined: {}", targetUrl);

        String redirectUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .build().toUriString();
        logger.info("Final redirect URL: {}", redirectUrl);

        response.sendRedirect(redirectUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request) {
        String redirectUri = request.getParameter("redirect_uri");
        logger.info("Redirect URI from parameter: {}", redirectUri);

        if (redirectUri == null && request.getSession() != null) {
            redirectUri = (String) request.getSession().getAttribute("REDIRECT_URI");
            logger.info("Redirect URI from session: {}", redirectUri);
            request.getSession().removeAttribute("REDIRECT_URI");
        }

        if (redirectUri != null && !redirectUri.isEmpty() && isAllowedRedirectPath(redirectUri)) {
            logger.info("Using redirect URI: {}", redirectUri);
            return URI + redirectUri;
        }

        logger.info("Using default success URL");
        return URI + DEFAULT_SUCCESS_URL;
    }

    private boolean isAllowedRedirectPath(String path) {
        return ALLOWED_REDIRECT_PATHS.stream()
                .anyMatch(allowedPath -> path.startsWith(allowedPath));
    }

    public String handleAuthentication(Authentication authentication) {
        String accessToken = tokenProvider.generateAccessToken(authentication);
        tokenProvider.generateRefreshToken(authentication, accessToken);
        return accessToken;
    }
}