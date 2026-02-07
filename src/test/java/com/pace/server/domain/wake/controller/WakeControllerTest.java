package com.pace.server.domain.wake.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.wake.dto.request.WakeLogRequest;
import com.pace.server.domain.wake.entity.WakeLog;
import com.pace.server.domain.wake.repository.WakeLogRepository;
import com.pace.server.support.IntegrationTestSupport;

class WakeControllerTest extends IntegrationTestSupport {

    @Autowired
    private WakeLogRepository wakeLogRepository;

    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        accessToken = createAccessToken(testUser);
    }

    @Nested
    @DisplayName("POST /api/v1/wake/log")
    class RecordWakeLog {

        @Test
        @DisplayName("성공: 기상 로그 기록")
        void success() throws Exception {
            // given
            WakeLogRequest request = new WakeLogRequest(LocalTime.of(7, 0), true, 30);

            // when & then
            mockMvc.perform(post("/api/v1/wake/log")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("CREATED"))
                    .andExpect(jsonPath("$.data.isSuccess").value(true))
                    .andExpect(jsonPath("$.data.durationSeconds").value(30));
        }

        @Test
        @DisplayName("실패: 미션 시간 너무 짧음")
        void fail_missionTooFast() throws Exception {
            // given
            WakeLogRequest request = new WakeLogRequest(LocalTime.of(7, 0), true, 0);

            // when & then
            mockMvc.perform(post("/api/v1/wake/log")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("E_MISSION_TOO_FAST"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/wake/streak")
    class GetStreak {

        @Test
        @DisplayName("성공: 스트릭 조회")
        void success() throws Exception {
            // given
            LocalDate today = LocalDate.now();
            wakeLogRepository.save(WakeLog.builder()
                    .user(testUser)
                    .wakeDate(today)
                    .wakeTime(LocalTime.of(7, 0))
                    .isSuccess(true)
                    .durationSeconds(30)
                    .build());
            wakeLogRepository.save(WakeLog.builder()
                    .user(testUser)
                    .wakeDate(today.minusDays(1))
                    .wakeTime(LocalTime.of(7, 0))
                    .isSuccess(true)
                    .durationSeconds(25)
                    .build());

            // when & then
            mockMvc.perform(get("/api/v1/wake/streak")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.currentStreak").value(2))
                    .andExpect(jsonPath("$.data.totalSuccessCount").value(2));
        }

        @Test
        @DisplayName("성공: 기록 없는 경우")
        void success_noLogs() throws Exception {
            mockMvc.perform(get("/api/v1/wake/streak")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.currentStreak").value(0))
                    .andExpect(jsonPath("$.data.totalSuccessCount").value(0));
        }
    }
}
