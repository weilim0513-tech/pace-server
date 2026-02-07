package com.pace.server.domain.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.pace.server.domain.member.dto.request.AddInterestRequest;
import com.pace.server.domain.member.entity.InterestItem;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.repository.InterestItemRepository;
import com.pace.server.support.IntegrationTestSupport;

/**
 * Interest API 통합 테스트
 */
class InterestControllerTest extends IntegrationTestSupport {

    @Autowired
    private InterestItemRepository interestItemRepository;

    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        accessToken = createAccessToken(testUser);
    }

    @Nested
    @DisplayName("GET /api/v1/members/me/interests")
    class GetInterests {

        @Test
        @DisplayName("성공: 빈 목록 조회")
        void success_empty() throws Exception {
            mockMvc.perform(get("/api/v1/members/me/interests")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("성공: 관심 항목 목록 조회")
        void success_withItems() throws Exception {
            // given
            interestItemRepository.save(InterestItem.builder()
                    .user(testUser)
                    .type(InterestItem.InterestType.KEYWORD)
                    .value("AI")
                    .build());

            interestItemRepository.save(InterestItem.builder()
                    .user(testUser)
                    .type(InterestItem.InterestType.STOCK)
                    .value("005930")
                    .build());

            // when & then
            mockMvc.perform(get("/api/v1/members/me/interests")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(2));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/members/me/interests")
    class AddInterest {

        @Test
        @DisplayName("성공: 키워드 추가")
        void success_keyword() throws Exception {
            AddInterestRequest request = new AddInterestRequest(
                    InterestItem.InterestType.KEYWORD, "블록체인");

            mockMvc.perform(post("/api/v1/members/me/interests")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("CREATED"))
                    .andExpect(jsonPath("$.data.type").value("KEYWORD"))
                    .andExpect(jsonPath("$.data.value").value("블록체인"));
        }

        @Test
        @DisplayName("성공: 주식 종목 추가")
        void success_stock() throws Exception {
            AddInterestRequest request = new AddInterestRequest(
                    InterestItem.InterestType.STOCK, "005930");

            mockMvc.perform(post("/api/v1/members/me/interests")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.type").value("STOCK"))
                    .andExpect(jsonPath("$.data.value").value("005930"));
        }

        @Test
        @DisplayName("실패: 유효성 검사 - 빈 값")
        void fail_validation() throws Exception {
            AddInterestRequest request = new AddInterestRequest(
                    InterestItem.InterestType.KEYWORD, "");

            mockMvc.perform(post("/api/v1/members/me/interests")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/members/me/interests/{itemId}")
    class DeleteInterest {

        @Test
        @DisplayName("성공: 관심 항목 삭제")
        void success() throws Exception {
            // given
            InterestItem saved = interestItemRepository.save(InterestItem.builder()
                    .user(testUser)
                    .type(InterestItem.InterestType.KEYWORD)
                    .value("삭제할항목")
                    .build());

            // when & then
            mockMvc.perform(delete("/api/v1/members/me/interests/{itemId}", saved.getId())
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("DELETED"));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 항목")
        void fail_notFound() throws Exception {
            mockMvc.perform(delete("/api/v1/members/me/interests/{itemId}", 99999L)
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }
}
