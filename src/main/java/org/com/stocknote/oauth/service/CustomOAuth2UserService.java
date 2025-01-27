package org.com.stocknote.oauth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.global.error.ErrorCode;
import org.com.stocknote.global.exception.CustomException;
import org.com.stocknote.oauth.entity.OAuth2UserInfo;
import org.com.stocknote.oauth.entity.PrincipalDetails;
import org.com.stocknote.domain.member.entity.Member;
import org.com.stocknote.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;


    @Transactional
    @Override
    public OAuth2User loadUser (OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();
        // 2. resistrationId 가져오기 (third-party id)

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 3. userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 유저 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);

        // 5. 회원가입 및 로그인
        Member member = getOrSave(oAuth2UserInfo);
        System.out.println(member);
        // 6. OAuth2User로 반환
        return new PrincipalDetails(member, oAuth2UserAttributes, userNameAttributeName);
    }

    private Member getOrSave (OAuth2UserInfo oAuth2UserInfo) {
        Member member = memberRepository.findByEmail(oAuth2UserInfo.email())
                .orElseGet(oAuth2UserInfo::toEntity);
        return memberRepository.save(member);
    }

    @Transactional
    public OAuth2User processOAuth2User(String registrationId, String code) {
        // 1. ClientRegistration 동적으로 가져오기
        ClientRegistration clientRegistration = getClientRegistration(registrationId);

        // 2. Access Token 요청 및 발급
        OAuth2AccessToken accessToken = getAccessToken(clientRegistration, code);

        // 3. UserInfo 요청 및 처리
        Map<String, Object> attributes = getUserInfo(clientRegistration, accessToken);

        // 4. OAuth2UserInfo 객체 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, attributes);

        // 5. 회원 저장 및 반환
        Member member = getOrSave(oAuth2UserInfo);
        return new PrincipalDetails(member, attributes, clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
    }

    private Map<String, Object> getUserInfo(ClientRegistration clientRegistration, OAuth2AccessToken accessToken) {
        // UserInfo Endpoint URI 가져오기
        String userInfoUri = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();

        // HTTP 요청 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken.getTokenValue()); // Access Token을 Authorization 헤더에 추가
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP 요청 생성
        HttpEntity<String> request = new HttpEntity<>(headers);

        // RestTemplate 사용
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                request,
                Map.class
        );

        // 응답 본문 반환
        return response.getBody();
    }

    private OAuth2AccessToken getAccessToken(ClientRegistration clientRegistration, String code) {
        // Access Token 요청을 위한 URI
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();

        // HTTP 요청 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디 생성
        String body = "grant_type=authorization_code" +
                "&client_id=" + clientRegistration.getClientId() +
                "&client_secret=" + clientRegistration.getClientSecret() +
                "&redirect_uri=" + clientRegistration.getRedirectUri() +
                "&code=" + code;

        // HTTP 요청 생성
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // RestTemplate을 사용하여 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                request,
                Map.class
        );

        // Access Token 추출
        Map<String, Object> responseBody = response.getBody();
        String tokenValue = (String) responseBody.get("access_token");
        long expiresIn = ((Number) responseBody.get("expires_in")).longValue();

        // OAuth2AccessToken 객체 생성
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                tokenValue,
                Instant.now(),
                Instant.now().plusSeconds(expiresIn)
        );
    }

    public ClientRegistration getClientRegistration(String registrationId) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new IllegalArgumentException("Invalid registrationId: " + registrationId);
        }
        return clientRegistration;
    }
}
