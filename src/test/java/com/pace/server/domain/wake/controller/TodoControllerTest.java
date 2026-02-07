package com.pace.server.domain.wake.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.wake.dto.request.CreateTodoRequest;
import com.pace.server.domain.wake.entity.Todo;
import com.pace.server.domain.wake.repository.TodoRepository;
import com.pace.server.support.IntegrationTestSupport;

class TodoControllerTest extends IntegrationTestSupport {

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
    @DisplayName("GET /api/v1/todos")
    class GetTodayTodos {

        @Test
        @DisplayName("성공: 오늘 투두 목록 조회")
        void success() throws Exception {
            // given
            todoRepository.save(Todo.builder()
                    .user(testUser)
                    .content("운동하기")
                    .targetDate(LocalDate.now())
                    .isDone(false)
                    .build());

            // when & then
            mockMvc.perform(get("/api/v1/todos")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].content").value("운동하기"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/todos")
    class CreateTodo {

        @Test
        @DisplayName("성공: 투두 생성")
        void success() throws Exception {
            // given
            CreateTodoRequest request = new CreateTodoRequest("책 읽기");

            // when & then
            mockMvc.perform(post("/api/v1/todos")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").value("책 읽기"))
                    .andExpect(jsonPath("$.data.isDone").value(false));
        }

        @Test
        @DisplayName("실패: 빈 내용")
        void fail_emptyContent() throws Exception {
            // given
            CreateTodoRequest request = new CreateTodoRequest("");

            // when & then
            mockMvc.perform(post("/api/v1/todos")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/todos/{id}/toggle")
    class ToggleTodo {

        @Test
        @DisplayName("성공: 투두 완료 토글")
        void success() throws Exception {
            // given
            Todo todo = todoRepository.save(Todo.builder()
                    .user(testUser)
                    .content("운동하기")
                    .targetDate(LocalDate.now())
                    .isDone(false)
                    .build());

            // when & then
            mockMvc.perform(patch("/api/v1/todos/" + todo.getId() + "/toggle")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isDone").value(true));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/todos/{id}/postpone")
    class PostponeTodo {

        @Test
        @DisplayName("성공: 투두 다음날로 미루기")
        void success() throws Exception {
            // given
            Todo todo = todoRepository.save(Todo.builder()
                    .user(testUser)
                    .content("운동하기")
                    .targetDate(LocalDate.now())
                    .isDone(false)
                    .build());

            // when & then
            mockMvc.perform(post("/api/v1/todos/" + todo.getId() + "/postpone")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.targetDate").value(LocalDate.now().plusDays(1).toString()));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/todos/{id}")
    class DeleteTodo {

        @Test
        @DisplayName("성공: 투두 삭제")
        void success() throws Exception {
            // given
            Todo todo = todoRepository.save(Todo.builder()
                    .user(testUser)
                    .content("운동하기")
                    .targetDate(LocalDate.now())
                    .isDone(false)
                    .build());

            // when & then
            mockMvc.perform(delete("/api/v1/todos/" + todo.getId())
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("DELETED"));
        }
    }
}
