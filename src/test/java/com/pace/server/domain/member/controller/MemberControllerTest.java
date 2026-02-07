package com.pace.server.domain.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.pace.server.domain.member.dto.request.UpdateProfileRequest;
import com.pace.server.domain.member.entity.User;
import com.pace.server.support.IntegrationTestSupport;

/**
 * Member API 통합 테스트
 */
class MemberControllerTest extends IntegrationTestSupport {

    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        accessToken = createAccessToken(testUser);
    }

    @Nested
    @DisplayName("GET /api/v1/members/me")
    class GetMyProfile {

        @Test
        @DisplayName("성공: 내 프로필 조회")
        void success() throws Exception {
            mockMvc.perform(get("/api/v1/members/me")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.email").value("test@test.com"))
                    .andExpect(jsonPath("$.data.nickname").value("테스터"));
        }

        @Test
        @DisplayName("실패: 인증 없이 요청")
        void fail_unauthorized() throws Exception {
            mockMvc.perform(get("/api/v1/members/me"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("실패: 잘못된 토큰")
        void fail_invalidToken() throws Exception {
            mockMvc.perform(get("/api/v1/members/me")
                    .header("Authorization", "Bearer invalid.token.here"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/members/me")
    class UpdateProfile {

        @Test
        @DisplayName("성공: 닉네임 수정")
        void success() throws Exception {
            UpdateProfileRequest request = new UpdateProfileRequest("새닉네임");

            mockMvc.perform(patch("/api/v1/members/me")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("UPDATED"))
                    .andExpect(jsonPath("$.data.nickname").value("새닉네임"));
        }

        @Test
        @DisplayName("실패: 닉네임 유효성 검사 (너무 짧음)")
        void fail_validation() throws Exception {
            UpdateProfileRequest request = new UpdateProfileRequest("A");

            mockMvc.perform(patch("/api/v1/members/me")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("E_INVALID_INPUT"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/members/me")
    class Withdraw {

        @Test
        @DisplayName("성공: 회원 탈퇴")
        void success() throws Exception {
            mockMvc.perform(delete("/api/v1/members/me")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("DELETED"));
        }
    }
}
