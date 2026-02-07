package com.pace.server.domain.auth.service;

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

import com.pace.server.domain.auth.dto.ReissueRequest;
import com.pace.server.domain.auth.dto.TokenResponse;
import com.pace.server.domain.member.entity.Provider;
import com.pace.server.domain.member.entity.Role;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.entity.UserStatus;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.AuthException;
import com.pace.server.global.security.handler.RefreshTokenStore;
import com.pace.server.global.security.jwt.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenStore refreshTokenStore;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private static final String VALID_REFRESH_TOKEN = "valid.refresh.token";
    private static final String NEW_ACCESS_TOKEN = "new.access.token";
    private static final String NEW_REFRESH_TOKEN = "new.refresh.token";

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
    @DisplayName("토큰 재발급")
    class Reissue {

        @Test
        @DisplayName("성공: 토큰이 정상적으로 재발급된다")
        void success() {
            // given
            ReissueRequest request = new ReissueRequest(VALID_REFRESH_TOKEN);

            given(jwtTokenProvider.validateToken(VALID_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUserIdFromToken(VALID_REFRESH_TOKEN)).willReturn(1L);
            given(refreshTokenStore.matches(1L, VALID_REFRESH_TOKEN)).willReturn(true);
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(jwtTokenProvider.createAccessToken(1L, "test@test.com", "ROLE_USER"))
                    .willReturn(NEW_ACCESS_TOKEN);
            given(jwtTokenProvider.createRefreshToken(1L)).willReturn(NEW_REFRESH_TOKEN);
            given(jwtTokenProvider.getRefreshTokenExpiry()).willReturn(604800000L);

            // when
            TokenResponse response = authService.reissue(request);

            // then
            assertThat(response.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);
            assertThat(response.refreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
            verify(refreshTokenStore).delete(1L);
            verify(refreshTokenStore).save(eq(1L), eq(NEW_REFRESH_TOKEN), anyLong());
        }

        @Test
        @DisplayName("실패: 유효하지 않은 Refresh Token")
        void fail_invalidToken() {
            // given
            ReissueRequest request = new ReissueRequest("invalid.token");
            given(jwtTokenProvider.validateToken("invalid.token")).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.reissue(request))
                    .isInstanceOf(AuthException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_INVALID);
        }

        @Test
        @DisplayName("실패: Redis 토큰 불일치")
        void fail_tokenMismatch() {
            // given
            ReissueRequest request = new ReissueRequest(VALID_REFRESH_TOKEN);

            given(jwtTokenProvider.validateToken(VALID_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUserIdFromToken(VALID_REFRESH_TOKEN)).willReturn(1L);
            given(refreshTokenStore.matches(1L, VALID_REFRESH_TOKEN)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.reissue(request))
                    .isInstanceOf(AuthException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        @Test
        @DisplayName("실패: 비활성화된 사용자")
        void fail_inactiveUser() {
            // given
            User inactiveUser = User.builder()
                    .id(2L)
                    .email("inactive@test.com")
                    .role(Role.ROLE_USER)
                    .status(UserStatus.INACTIVE)
                    .build();

            ReissueRequest request = new ReissueRequest(VALID_REFRESH_TOKEN);

            given(jwtTokenProvider.validateToken(VALID_REFRESH_TOKEN)).willReturn(true);
            given(jwtTokenProvider.getUserIdFromToken(VALID_REFRESH_TOKEN)).willReturn(2L);
            given(refreshTokenStore.matches(2L, VALID_REFRESH_TOKEN)).willReturn(true);
            given(userRepository.findById(2L)).willReturn(Optional.of(inactiveUser));

            // when & then
            assertThatThrownBy(() -> authService.reissue(request))
                    .isInstanceOf(AuthException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_INACTIVE);
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("성공: Refresh Token이 삭제된다")
        void success() {
            // when
            authService.logout(1L);

            // then
            verify(refreshTokenStore).delete(1L);
        }
    }
}
