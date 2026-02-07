package com.pace.server.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pace.server.domain.member.dto.request.UpdateProfileRequest;
import com.pace.server.domain.member.dto.response.MemberResponse;
import com.pace.server.domain.member.entity.Provider;
import com.pace.server.domain.member.entity.Role;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.entity.UserStatus;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

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
    @DisplayName("프로필 조회")
    class GetProfile {

        @Test
        @DisplayName("성공: 프로필이 정상적으로 조회된다")
        void success() {
            // given
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            // when
            MemberResponse response = memberService.getProfile(1L);

            // then
            assertThat(response.userId()).isEqualTo(1L);
            assertThat(response.email()).isEqualTo("test@test.com");
            assertThat(response.nickname()).isEqualTo("테스터");
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자")
        void fail_userNotFound() {
            // given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.getProfile(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 비활성화된 사용자")
        void fail_inactiveUser() {
            // given
            User inactiveUser = User.builder()
                    .id(2L)
                    .email("inactive@test.com")
                    .status(UserStatus.INACTIVE)
                    .build();
            given(userRepository.findById(2L)).willReturn(Optional.of(inactiveUser));

            // when & then
            assertThatThrownBy(() -> memberService.getProfile(2L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("프로필 수정")
    class UpdateProfile {

        @Test
        @DisplayName("성공: 닉네임이 정상적으로 수정된다")
        void success() {
            // given
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            UpdateProfileRequest request = new UpdateProfileRequest("새닉네임");

            // when
            MemberResponse response = memberService.updateProfile(1L, request);

            // then
            assertThat(response.nickname()).isEqualTo("새닉네임");
        }

        @Test
        @DisplayName("성공: null 닉네임 요청 시 변경하지 않음")
        void success_nullNickname() {
            // given
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            UpdateProfileRequest request = new UpdateProfileRequest(null);

            // when
            MemberResponse response = memberService.updateProfile(1L, request);

            // then
            assertThat(response.nickname()).isEqualTo("테스터");
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    class Withdraw {

        @Test
        @DisplayName("성공: 회원이 비활성화된다")
        void success() {
            // given
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            // when
            memberService.withdraw(1L);

            // then
            assertThat(testUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }
    }
}
