package com.pace.server.domain.recap.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.wake.entity.Todo;
import com.pace.server.domain.wake.repository.TodoRepository;
import com.pace.server.support.IntegrationTestSupport;

@DisplayName("RecapController 통합 테스트")
class RecapControllerTest extends IntegrationTestSupport {

    @Autowired
    private TodoRepository todoRepository;

    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        accessToken = createAccessToken(testUser);
    }

    @Nested
    @DisplayName("GET /api/v1/recap")
    class GetRecap {

        @Test
        @DisplayName("성공: 오늘의 모닝 리캡 조회")
        void success() throws Exception {
            // given
            Todo todo = todoRepository.save(Todo.builder()
                    .user(testUser)
                    .content("통합 테스트 할일")
                    .targetDate(LocalDate.now())
                    .isDone(false)
                    .build());

            // when & then
            mockMvc.perform(get("/api/v1/recap")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.date").value(LocalDate.now().toString()))
                    .andExpect(jsonPath("$.data.todos").isArray())
                    .andExpect(jsonPath("$.data.todos[0]").value("통합 테스트 할일"))
                    .andExpect(jsonPath("$.data.weather").exists())
                    .andExpect(jsonPath("$.data.motivationMessage").isNotEmpty());
        }

        @Test
        @DisplayName("성공: 투두 없이 조회")
        void success_noTodos() throws Exception {
            // when & then
            mockMvc.perform(get("/api/v1/recap")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.todos").isArray())
                    .andExpect(jsonPath("$.data.todos").isEmpty());
        }

        @Test
        @DisplayName("실패: 인증 없이 요청")
        void fail_unauthorized() throws Exception {
            // when & then
            mockMvc.perform(get("/api/v1/recap")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }
}
