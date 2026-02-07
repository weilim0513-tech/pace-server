package com.pace.server.domain.wake.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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
import com.pace.server.domain.wake.dto.request.CreateAlarmRequest;
import com.pace.server.domain.wake.dto.request.UpdateAlarmRequest;
import com.pace.server.domain.wake.dto.response.AlarmResponse;
import com.pace.server.domain.wake.entity.Alarm;
import com.pace.server.domain.wake.entity.MissionType;
import com.pace.server.domain.wake.repository.AlarmRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @InjectMocks
    private AlarmService alarmService;

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private Alarm testAlarm;

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

        testAlarm = Alarm.builder()
                .id(1L)
                .user(testUser)
                .wakeTime(LocalTime.of(7, 0))
                .repeatDays("0111110")
                .missionType(MissionType.MATH)
                .missionLevel(2)
                .isActive(true)
                .label("출근 알람")
                .build();
    }

    @Nested
    @DisplayName("알람 목록 조회")
    class GetAlarms {

        @Test
        @DisplayName("성공: 알람 목록이 정상적으로 조회된다")
        void success() {
            // given
            given(alarmRepository.findByUserIdOrderByWakeTimeAsc(1L))
                    .willReturn(List.of(testAlarm));

            // when
            List<AlarmResponse> result = alarmService.getAlarms(1L);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).wakeTime()).isEqualTo(LocalTime.of(7, 0));
        }

        @Test
        @DisplayName("성공: 알람이 없으면 빈 목록 반환")
        void success_emptyList() {
            // given
            given(alarmRepository.findByUserIdOrderByWakeTimeAsc(1L))
                    .willReturn(List.of());

            // when
            List<AlarmResponse> result = alarmService.getAlarms(1L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("알람 생성")
    class CreateAlarm {

        @Test
        @DisplayName("성공: 알람이 정상적으로 생성된다")
        void success() {
            // given
            CreateAlarmRequest request = new CreateAlarmRequest(
                    LocalTime.of(6, 30),
                    "1111111",
                    MissionType.SHAKE,
                    3,
                    "새 알람");

            given(alarmRepository.countByUserId(1L)).willReturn(0L);
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(alarmRepository.save(any(Alarm.class))).willAnswer(invocation -> {
                Alarm alarm = invocation.getArgument(0);
                return Alarm.builder()
                        .id(2L)
                        .user(alarm.getUser())
                        .wakeTime(alarm.getWakeTime())
                        .repeatDays(alarm.getRepeatDays())
                        .missionType(alarm.getMissionType())
                        .missionLevel(alarm.getMissionLevel())
                        .label(alarm.getLabel())
                        .isActive(true)
                        .build();
            });

            // when
            AlarmResponse result = alarmService.createAlarm(1L, request);

            // then
            assertThat(result.wakeTime()).isEqualTo(LocalTime.of(6, 30));
            assertThat(result.missionType()).isEqualTo(MissionType.SHAKE);
            then(alarmRepository).should().save(any(Alarm.class));
        }

        @Test
        @DisplayName("실패: 알람 개수 10개 초과")
        void fail_limitExceeded() {
            // given
            CreateAlarmRequest request = new CreateAlarmRequest(
                    LocalTime.of(6, 30), "1111111", MissionType.MATH, 1, null);
            given(alarmRepository.countByUserId(1L)).willReturn(10L);

            // when & then
            assertThatThrownBy(() -> alarmService.createAlarm(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALARM_LIMIT_EXCEEDED);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자")
        void fail_userNotFound() {
            // given
            CreateAlarmRequest request = new CreateAlarmRequest(
                    LocalTime.of(6, 30), "1111111", MissionType.MATH, 1, null);
            given(alarmRepository.countByUserId(999L)).willReturn(0L);
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> alarmService.createAlarm(999L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("알람 수정")
    class UpdateAlarm {

        @Test
        @DisplayName("성공: 알람이 정상적으로 수정된다")
        void success() {
            // given
            UpdateAlarmRequest request = new UpdateAlarmRequest(
                    LocalTime.of(8, 0), "0111110", MissionType.SQUAT, 4, "수정된 라벨");
            given(alarmRepository.findById(1L)).willReturn(Optional.of(testAlarm));

            // when
            AlarmResponse result = alarmService.updateAlarm(1L, 1L, request);

            // then
            assertThat(result.wakeTime()).isEqualTo(LocalTime.of(8, 0));
            assertThat(result.missionType()).isEqualTo(MissionType.SQUAT);
        }

        @Test
        @DisplayName("실패: 다른 사용자의 알람")
        void fail_notOwner() {
            // given
            User anotherUser = User.builder().id(2L).build();
            Alarm anotherAlarm = Alarm.builder().id(2L).user(anotherUser).build();

            UpdateAlarmRequest request = new UpdateAlarmRequest(
                    LocalTime.of(8, 0), "0111110", MissionType.MATH, 1, null);
            given(alarmRepository.findById(2L)).willReturn(Optional.of(anotherAlarm));

            // when & then
            assertThatThrownBy(() -> alarmService.updateAlarm(1L, 2L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALARM_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("알람 토글")
    class ToggleAlarm {

        @Test
        @DisplayName("성공: 알람이 비활성화된다")
        void success_deactivate() {
            // given
            given(alarmRepository.findById(1L)).willReturn(Optional.of(testAlarm));

            // when
            AlarmResponse result = alarmService.toggleAlarm(1L, 1L);

            // then
            assertThat(result.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("알람 삭제")
    class DeleteAlarm {

        @Test
        @DisplayName("성공: 알람이 정상적으로 삭제된다")
        void success() {
            // given
            given(alarmRepository.findById(1L)).willReturn(Optional.of(testAlarm));

            // when
            alarmService.deleteAlarm(1L, 1L);

            // then
            then(alarmRepository).should().delete(testAlarm);
        }
    }
}
