package com.pace.server.domain.wake.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.wake.dto.request.CreateAlarmRequest;
import com.pace.server.domain.wake.dto.request.UpdateAlarmRequest;
import com.pace.server.domain.wake.entity.Alarm;
import com.pace.server.domain.wake.entity.MissionType;
import com.pace.server.domain.wake.repository.AlarmRepository;
import com.pace.server.support.IntegrationTestSupport;

class AlarmControllerTest extends IntegrationTestSupport {

    @Autowired
    private AlarmRepository alarmRepository;

    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        accessToken = createAccessToken(testUser);
    }

    @Nested
    @DisplayName("GET /api/v1/alarms")
    class GetAlarms {

        @Test
        @DisplayName("성공: 알람 목록 조회")
        void success() throws Exception {
            // given
            alarmRepository.save(Alarm.builder()
                    .user(testUser)
                    .wakeTime(LocalTime.of(7, 0))
                    .repeatDays("0111110")
                    .missionType(MissionType.MATH)
                    .missionLevel(2)
                    .isActive(true)
                    .build());

            // when & then
            mockMvc.perform(get("/api/v1/alarms")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].wakeTime").value("07:00:00"));
        }

        @Test
        @DisplayName("실패: 인증 없이 요청")
        void fail_unauthorized() throws Exception {
            mockMvc.perform(get("/api/v1/alarms"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/alarms")
    class CreateAlarm {

        @Test
        @DisplayName("성공: 알람 생성")
        void success() throws Exception {
            // given
            CreateAlarmRequest request = new CreateAlarmRequest(
                    LocalTime.of(6, 30),
                    "1111111",
                    MissionType.SHAKE,
                    3,
                    "출근 알람");

            // when & then
            mockMvc.perform(post("/api/v1/alarms")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("CREATED"))
                    .andExpect(jsonPath("$.data.wakeTime").value("06:30:00"))
                    .andExpect(jsonPath("$.data.missionType").value("SHAKE"));
        }

        @Test
        @DisplayName("실패: 유효하지 않은 반복 요일")
        void fail_invalidRepeatDays() throws Exception {
            // given
            CreateAlarmRequest request = new CreateAlarmRequest(
                    LocalTime.of(6, 30),
                    "12345", // 잘못된 형식
                    MissionType.MATH,
                    1,
                    null);

            // when & then
            mockMvc.perform(post("/api/v1/alarms")
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/alarms/{id}")
    class UpdateAlarm {

        @Test
        @DisplayName("성공: 알람 수정")
        void success() throws Exception {
            // given
            Alarm alarm = alarmRepository.save(Alarm.builder()
                    .user(testUser)
                    .wakeTime(LocalTime.of(7, 0))
                    .repeatDays("0111110")
                    .missionType(MissionType.MATH)
                    .missionLevel(2)
                    .build());

            UpdateAlarmRequest request = new UpdateAlarmRequest(
                    LocalTime.of(8, 0),
                    "1111111",
                    MissionType.SQUAT,
                    4,
                    "수정된 알람");

            // when & then
            mockMvc.perform(put("/api/v1/alarms/" + alarm.getId())
                    .header("Authorization", bearerToken(accessToken))
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.wakeTime").value("08:00:00"))
                    .andExpect(jsonPath("$.data.missionType").value("SQUAT"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/alarms/{id}/toggle")
    class ToggleAlarm {

        @Test
        @DisplayName("성공: 알람 토글")
        void success() throws Exception {
            // given
            Alarm alarm = alarmRepository.save(Alarm.builder()
                    .user(testUser)
                    .wakeTime(LocalTime.of(7, 0))
                    .repeatDays("0111110")
                    .missionType(MissionType.MATH)
                    .missionLevel(1)
                    .isActive(true)
                    .build());

            // when & then
            mockMvc.perform(patch("/api/v1/alarms/" + alarm.getId() + "/toggle")
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isActive").value(false));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/alarms/{id}")
    class DeleteAlarm {

        @Test
        @DisplayName("성공: 알람 삭제")
        void success() throws Exception {
            // given
            Alarm alarm = alarmRepository.save(Alarm.builder()
                    .user(testUser)
                    .wakeTime(LocalTime.of(7, 0))
                    .repeatDays("0111110")
                    .missionType(MissionType.MATH)
                    .missionLevel(1)
                    .build());

            // when & then
            mockMvc.perform(delete("/api/v1/alarms/" + alarm.getId())
                    .header("Authorization", bearerToken(accessToken)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("DELETED"));
        }
    }
}
