package org.com.stocknote.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.oauth.service.CustomOAuth2UserService;
import org.com.stocknote.oauth.token.TokenAuthenticationFilter;
import org.com.stocknote.oauth.token.TokenExceptionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
        return web -> web.ignoring()
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/error",
                        "/swagger-ui/oauth2-redirect.html",
                        "/favicon.ico"
                );
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // rest api 설정
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화 -> cookie를 사용하지 않으면 꺼도 된다. (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.)
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 login form 비활성화
                .logout(AbstractHttpConfigurer::disable) // 기본 logout 비활성화
                .headers(c -> c.frameOptions(
                        FrameOptionsConfig::disable).disable()) // X-Frame-Options 비활성화
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))// 세션 사용하지 않음

                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // request 인증, 인가 설정
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/",
                                "/api/filtered/*",
                                "/api/kospi",
                                "/api/kosdaq",
                                "/api/kospi200",
                                "/api/volume",
                                "/swagger-ui/**",
                                "/swagger-ui/oauth2-redirect.html",// Swagger UI 경로 허용
                                "/v3/api-docs/**",
                                "/auth/kakao/callback",
                                "/auth/**",
                                "/auth/google/manual",
                                "/auth/kakao/manual",
                                "/auth/google/token",
                                "/oauth2.googleapis.com/token",
                                "/api/v1/post/*",
                                "/api/v1/post/**",
                                "/api/v1/post"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // oauth2 설정
                .oauth2Login(oauth ->
                        oauth.authorizationEndpoint(endpoint -> endpoint
                                        .authorizationRequestRepository(
                                                new HttpSessionOAuth2AuthorizationRequestRepository()
                                        )
                                        .baseUri("/oauth2/authorization")
                                )
                                .userInfoEndpoint(c -> c.userService(oAuth2UserService))
                                .successHandler(oAuth2SuccessHandler)
                                .redirectionEndpoint(e -> e
                                        .baseUri("/auth/{registrationId}/redirect")
                                )
                                .failureHandler((request, response, exception) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json;charset=UTF-8");

                                    Map<String, String> errorDetails = new HashMap<>();
                                    errorDetails.put("error", "Authentication failed");
                                    errorDetails.put("message", exception.getMessage());

                                    String errorJson = new ObjectMapper().writeValueAsString(errorDetails);
                                    response.getWriter().write(errorJson);
                                })
                )

                // jwt 관련 설정
                .addFilterBefore(tokenAuthenticationFilter, //tokenAuthenticationFilter: JWT 인증을 처리하는 커스텀 필터, 모든 요청에서 토큰을 검사하고 인증 여부를 결정
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass()) // 토큰 예외 핸들링

                // 인증/인가 예외 핸들링
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 인증 실패 시 처리
                        .accessDeniedHandler(new CustomAccessDeniedHandler())); //인가 실패 시 처리

        return http.build();
    }
}
