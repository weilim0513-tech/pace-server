package com.pace.server.domain.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.pace.server.domain.auth.dto.ReissueRequest;
import com.pace.server.domain.member.entity.User;
import com.pace.server.global.security.handler.RefreshTokenStore;
import com.pace.server.support.IntegrationTestSupport;

/**
 * Auth API 통합 테스트
 */
class AuthControllerTest extends IntegrationTestSupport {

    @Autowired
    private RefreshTokenStore refreshTokenStore;

    private User testUser;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        accessToken = createAccessToken(testUser);
        refreshToken = jwtTokenProvider.createRefreshToken(testUser.getId());

        // Redis에 Refresh Token 저장
        refreshTokenStore.save(
                testUser.getId(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenExpiry());
    }

    @Nested
    @DisplayName("POST /api/v1/auth/reissue")
    class Reissue {

        @Test
        @DisplayName("성공: 토큰 재발급")
        void success() throws Exception {
            ReissueRequest request = new ReissueRequest(refreshToken);

            mockMvc.perform(post("/api/v1/auth/reissue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("TOKEN_REISSUED"))
                    .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
        }

        @Test
        @DisplayName("실패: 유효하지 않은 Refresh Token")
        void fail_invalidToken() throws Exception {
            ReissueRequest request = new ReissueRequest("invalid.refresh.token");

            mockMvc.perform(post("/api/v1/auth/reissue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("E_TOKEN_INVALID"));
        }

        @Test
        @DisplayName("실패: Redis에 저장되지 않은 토큰")
        void fail_tokenNotInRedis() throws Exception {
            // given - 새 토큰 생성 (Redis에 저장 안함)
            String notStoredToken = jwtTokenProvider.createRefreshToken(testUser.getId());
            // Redis에서 기존 토큰 삭제
            refreshTokenStore.delete(testUser.getId());

            ReissueRequest request = new ReissueRequest(notStoredToken);

            mockMvc.perform(post("/api/v1/auth/reissue")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("E_RT_MISMATCH"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/logout")
    class Logout {

        @Test
        @DisplayName("성공: 로그아웃")
        void success() throws Exception {
            mockMvc.perform(post("/api/v1/auth/logout")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("LOGOUT_SUCCESS"));
        }

        @Test
        @DisplayName("실패: 인증 없이 로그아웃")
        void fail_unauthorized() throws Exception {
            mockMvc.perform(post("/api/v1/auth/logout"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }
}
