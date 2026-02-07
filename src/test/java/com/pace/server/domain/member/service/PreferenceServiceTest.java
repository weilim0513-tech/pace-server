package com.pace.server.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pace.server.domain.member.dto.request.UpdatePreferenceRequest;
import com.pace.server.domain.member.dto.response.PreferenceResponse;
import com.pace.server.domain.member.entity.Provider;
import com.pace.server.domain.member.entity.Role;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.entity.UserPreference;
import com.pace.server.domain.member.entity.UserStatus;
import com.pace.server.domain.member.repository.UserPreferenceRepository;
import com.pace.server.domain.member.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PreferenceServiceTest {

    @InjectMocks
    private PreferenceService preferenceService;

    @Mock
    private UserPreferenceRepository preferenceRepository;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private UserPreference testPreference;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .provider(Provider.KAKAO)
                .providerId("12345")
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        testPreference = UserPreference.builder()
                .id(1L)
                .user(testUser)
                .regionCode("1100000000")
                .regionName("서울특별시")
                .voiceEnabled(true)
                .targetBriefingTime(LocalTime.of(7, 0))
                .build();
    }

    @Nested
    @DisplayName("설정 조회")
    class GetPreference {

        @Test
        @DisplayName("성공: 기존 설정이 조회된다")
        void success_existing() {
            // given
            given(preferenceRepository.findByUserId(1L)).willReturn(Optional.of(testPreference));

            // when
            PreferenceResponse response = preferenceService.getPreference(1L);

            // then
            assertThat(response.prefId()).isEqualTo(1L);
            assertThat(response.regionCode()).isEqualTo("1100000000");
            assertThat(response.regionName()).isEqualTo("서울특별시");
            assertThat(response.voiceEnabled()).isTrue();
        }

        @Test
        @DisplayName("성공: 설정이 없으면 기본값으로 생성된다")
        void success_createDefault() {
            // given
            given(preferenceRepository.findByUserId(1L)).willReturn(Optional.empty());
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(preferenceRepository.save(any(UserPreference.class))).willAnswer(invocation -> {
                UserPreference saved = invocation.getArgument(0);
                return UserPreference.builder()
                        .id(1L)
                        .user(saved.getUser())
                        .voiceEnabled(saved.isVoiceEnabled())
                        .build();
            });

            // when
            PreferenceResponse response = preferenceService.getPreference(1L);

            // then
            assertThat(response.voiceEnabled()).isTrue();
            verify(preferenceRepository).save(any(UserPreference.class));
        }
    }

    @Nested
    @DisplayName("설정 수정")
    class UpdatePreference {

        @Test
        @DisplayName("성공: 지역 정보가 수정된다")
        void success_updateRegion() {
            // given
            given(preferenceRepository.findByUserId(1L)).willReturn(Optional.of(testPreference));
            UpdatePreferenceRequest request = new UpdatePreferenceRequest(
                    "2600000000", "부산광역시", null, null);

            // when
            PreferenceResponse response = preferenceService.updatePreference(1L, request);

            // then
            assertThat(response.regionCode()).isEqualTo("2600000000");
            assertThat(response.regionName()).isEqualTo("부산광역시");
        }

        @Test
        @DisplayName("성공: 음성 설정이 수정된다")
        void success_updateVoice() {
            // given
            given(preferenceRepository.findByUserId(1L)).willReturn(Optional.of(testPreference));
            UpdatePreferenceRequest request = new UpdatePreferenceRequest(
                    null, null, false, null);

            // when
            PreferenceResponse response = preferenceService.updatePreference(1L, request);

            // then
            assertThat(response.voiceEnabled()).isFalse();
        }

        @Test
        @DisplayName("성공: 브리핑 시간이 수정된다")
        void success_updateBriefingTime() {
            // given
            given(preferenceRepository.findByUserId(1L)).willReturn(Optional.of(testPreference));
            UpdatePreferenceRequest request = new UpdatePreferenceRequest(
                    null, null, null, LocalTime.of(8, 30));

            // when
            PreferenceResponse response = preferenceService.updatePreference(1L, request);

            // then
            assertThat(response.targetBriefingTime()).isEqualTo(LocalTime.of(8, 30));
        }
    }
}
