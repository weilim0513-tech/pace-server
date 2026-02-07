package com.pace.server.domain.wake.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pace.server.domain.member.entity.Provider;
import com.pace.server.domain.member.entity.Role;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.entity.UserStatus;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.domain.wake.dto.request.WakeLogRequest;
import com.pace.server.domain.wake.dto.response.StreakResponse;
import com.pace.server.domain.wake.dto.response.WakeLogResponse;
import com.pace.server.domain.wake.entity.WakeLog;
import com.pace.server.domain.wake.repository.WakeLogRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class WakeLogServiceTest {

    @InjectMocks
    private WakeLogService wakeLogService;

    @Mock
    private WakeLogRepository wakeLogRepository;

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("테스터")
                .provider(Provider.KAKAO)
                .providerId("12345")
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("기상 로그 기록")
    class RecordWakeLog {

        @Test
        @DisplayName("성공: 기상 로그가 정상적으로 기록된다")
        void success() {
            // given
            WakeLogRequest request = new WakeLogRequest(LocalTime.of(7, 0), true, 30);
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(wakeLogRepository.save(any(WakeLog.class))).willAnswer(invocation -> {
                WakeLog log = invocation.getArgument(0);
                return WakeLog.builder()
                        .id(1L)
                        .user(log.getUser())
                        .wakeDate(LocalDate.now())
                        .wakeTime(log.getWakeTime())
                        .isSuccess(log.isSuccess())
                        .durationSeconds(log.getDurationSeconds())
                        .build();
            });

            // when
            WakeLogResponse result = wakeLogService.recordWakeLog(1L, request);

            // then
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.durationSeconds()).isEqualTo(30);
            then(wakeLogRepository).should().save(any(WakeLog.class));
        }

        @Test
        @DisplayName("실패: 미션 수행 시간이 너무 짧음 (어뷰징)")
        void fail_missionTooFast() {
            // given
            WakeLogRequest request = new WakeLogRequest(LocalTime.of(7, 0), true, 0);

            // when & then
            assertThatThrownBy(() -> wakeLogService.recordWakeLog(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MISSION_TOO_FAST);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자")
        void fail_userNotFound() {
            // given
            WakeLogRequest request = new WakeLogRequest(LocalTime.of(7, 0), true, 30);
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> wakeLogService.recordWakeLog(999L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("연속 기상 스트릭 조회")
    class GetStreak {

        @Test
        @DisplayName("성공: 연속 3일 스트릭")
        void success_threeDay() {
            // given
            LocalDate today = LocalDate.now();
            List<WakeLog> logs = List.of(
                    createWakeLog(today),
                    createWakeLog(today.minusDays(1)),
                    createWakeLog(today.minusDays(2)));

            given(wakeLogRepository.findByUserIdAndIsSuccessTrueOrderByWakeDateDesc(1L))
                    .willReturn(logs);
            given(wakeLogRepository.countByUserIdAndIsSuccessTrue(1L))
                    .willReturn(10L);

            // when
            StreakResponse result = wakeLogService.getStreak(1L);

            // then
            assertThat(result.currentStreak()).isEqualTo(3);
            assertThat(result.totalSuccessCount()).isEqualTo(10);
        }

        @Test
        @DisplayName("성공: 어제부터 시작하는 스트릭")
        void success_fromYesterday() {
            // given
            LocalDate today = LocalDate.now();
            List<WakeLog> logs = List.of(
                    createWakeLog(today.minusDays(1)),
                    createWakeLog(today.minusDays(2)));

            given(wakeLogRepository.findByUserIdAndIsSuccessTrueOrderByWakeDateDesc(1L))
                    .willReturn(logs);
            given(wakeLogRepository.countByUserIdAndIsSuccessTrue(1L))
                    .willReturn(2L);

            // when
            StreakResponse result = wakeLogService.getStreak(1L);

            // then
            assertThat(result.currentStreak()).isEqualTo(2);
        }

        @Test
        @DisplayName("성공: 스트릭 끊김 (2일 전 기록 없음)")
        void success_brokenStreak() {
            // given
            LocalDate today = LocalDate.now();
            List<WakeLog> logs = List.of(
                    createWakeLog(today),
                    createWakeLog(today.minusDays(3)) // 2일 건너뜀
            );

            given(wakeLogRepository.findByUserIdAndIsSuccessTrueOrderByWakeDateDesc(1L))
                    .willReturn(logs);
            given(wakeLogRepository.countByUserIdAndIsSuccessTrue(1L))
                    .willReturn(2L);

            // when
            StreakResponse result = wakeLogService.getStreak(1L);

            // then
            assertThat(result.currentStreak()).isEqualTo(1);
        }

        @Test
        @DisplayName("성공: 기록이 없는 경우")
        void success_noLogs() {
            // given
            given(wakeLogRepository.findByUserIdAndIsSuccessTrueOrderByWakeDateDesc(1L))
                    .willReturn(List.of());
            given(wakeLogRepository.countByUserIdAndIsSuccessTrue(1L))
                    .willReturn(0L);

            // when
            StreakResponse result = wakeLogService.getStreak(1L);

            // then
            assertThat(result.currentStreak()).isZero();
            assertThat(result.maxStreak()).isZero();
            assertThat(result.totalSuccessCount()).isZero();
        }

        private WakeLog createWakeLog(LocalDate date) {
            return WakeLog.builder()
                    .id(1L)
                    .user(testUser)
                    .wakeDate(date)
                    .wakeTime(LocalTime.of(7, 0))
                    .isSuccess(true)
                    .durationSeconds(30)
                    .build();
        }
    }
}
