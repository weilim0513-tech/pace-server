package com.pace.server.global.security.jwt;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * JWT Token Provider 단위 테스트
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    // 테스트용 Secret (Base64 인코딩된 64바이트 이상)
    private static final String TEST_SECRET = "cGFjZS13YWtlLXVsdHJhLXNlY3VyZS1qd3Qtc2VjcmV0LWtleS0yMDI2LXByb2R1Y3Rpb24tZW52aXJvbm1lbnQ=";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                TEST_SECRET,
                3600000L, // 1시간
                604800000L // 7일
        );
    }

    @Nested
    @DisplayName("Access Token 생성")
    class CreateAccessToken {

        @Test
        @DisplayName("성공: Access Token이 정상적으로 생성된다")
        void success() {
            // given
            Long userId = 1L;
            String email = "test@test.com";
            String role = "ROLE_USER";

            // when
            String token = jwtTokenProvider.createAccessToken(userId, email, role);

            // then
            assertThat(token).isNotBlank();
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("성공: 토큰에서 userId를 추출할 수 있다")
        void extractUserId() {
            // given
            Long userId = 123L;
            String token = jwtTokenProvider.createAccessToken(userId, "test@test.com", "ROLE_USER");

            // when
            Long extractedId = jwtTokenProvider.getUserIdFromToken(token);

            // then
            assertThat(extractedId).isEqualTo(userId);
        }

        @Test
        @DisplayName("성공: 토큰에서 email을 추출할 수 있다")
        void extractEmail() {
            // given
            String email = "user@example.com";
            String token = jwtTokenProvider.createAccessToken(1L, email, "ROLE_USER");

            // when
            String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

            // then
            assertThat(extractedEmail).isEqualTo(email);
        }
    }

    @Nested
    @DisplayName("Refresh Token 생성")
    class CreateRefreshToken {

        @Test
        @DisplayName("성공: Refresh Token이 정상적으로 생성된다")
        void success() {
            // given
            Long userId = 1L;

            // when
            String token = jwtTokenProvider.createRefreshToken(userId);

            // then
            assertThat(token).isNotBlank();
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    class ValidateToken {

        @Test
        @DisplayName("실패: 잘못된 형식의 토큰")
        void fail_malformed() {
            // given
            String malformedToken = "malformed.token.here";

            // when
            boolean result = jwtTokenProvider.validateToken(malformedToken);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("실패: 빈 토큰")
        void fail_empty() {
            // when
            boolean result = jwtTokenProvider.validateToken("");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("실패: 만료된 토큰")
        void fail_expired() throws InterruptedException {
            // given - 1ms 만료 토큰 생성
            JwtTokenProvider shortExpiryProvider = new JwtTokenProvider(
                    TEST_SECRET,
                    1L, // 1ms
                    1L);
            String token = shortExpiryProvider.createAccessToken(1L, "test@test.com", "ROLE_USER");

            // when - 토큰 만료 대기
            Thread.sleep(10);

            // then
            assertThat(shortExpiryProvider.validateToken(token)).isFalse();
        }
    }
}
