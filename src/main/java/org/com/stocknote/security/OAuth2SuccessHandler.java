package org.com.stocknote.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.config.AppConfig;
import org.com.stocknote.oauth.token.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private static final String URI = AppConfig.getSiteFrontUrl();
    private static final String DEFAULT_SUCCESS_URL = "/";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String accessToken = handleAuthentication(authentication);
        String targetUrl = determineTargetUrl(request);

        String redirectUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request) {
        // URL 파라미터에서 redirect_uri를 확인
        String redirectUri = request.getParameter("redirect_uri");

        // 세션에서 저장된 URL 확인
        if (redirectUri == null && request.getSession() != null) {
            redirectUri = (String) request.getSession().getAttribute("REDIRECT_URI");
            request.getSession().removeAttribute("REDIRECT_URI");
        }

        // redirect_uri가 있으면 해당 URL로, 없으면 기본 URL로
        if (redirectUri != null && !redirectUri.isEmpty()) {
            return URI + redirectUri;
        }

        return URI + DEFAULT_SUCCESS_URL;
    }

    public String handleAuthentication(Authentication authentication) {
        String accessToken = tokenProvider.generateAccessToken(authentication);
        tokenProvider.generateRefreshToken(authentication, accessToken);
        return accessToken;
    }
}
