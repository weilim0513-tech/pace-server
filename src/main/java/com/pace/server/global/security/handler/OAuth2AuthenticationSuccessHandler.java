package com.pace.server.global.security.handler;

import com.pace.server.global.security.jwt.JwtTokenProvider;
import com.pace.server.global.security.oauth2.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                oauth2User.getUserId(),
                oauth2User.getEmail(),
                oauth2User.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(oauth2User.getUserId());

        // Refresh Token 저장 (Redis)
        refreshTokenStore.save(oauth2User.getUserId(), refreshToken,
                jwtTokenProvider.getRefreshTokenExpiry());

        log.info("OAuth2 로그인 성공 - userId: {}, provider: {}, isNewUser: {}",
                oauth2User.getUserId(), oauth2User.getProvider(), oauth2User.isNewUser());

        // 프론트엔드로 리다이렉트 (쿼리 파라미터로 토큰 전달)
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("isNewUser", oauth2User.isNewUser())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
