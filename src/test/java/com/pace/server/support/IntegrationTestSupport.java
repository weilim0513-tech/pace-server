package com.pace.server.support;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pace.server.domain.member.entity.Provider;
import com.pace.server.domain.member.entity.Role;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.entity.UserStatus;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.global.security.jwt.JwtTokenProvider;

/**
 * 통합 테스트 베이스 클래스.
 * Testcontainers를 사용하여 Redis 자동 실행.
 * H2 인메모리 DB 사용, 트랜잭션 롤백.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfig.class)
@Transactional
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected UserRepository userRepository;

    @BeforeEach
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    /**
     * 테스트용 사용자 생성
     */
    protected User createTestUser() {
        return userRepository.save(User.builder()
                .email("test@test.com")
                .nickname("테스터")
                .provider(Provider.KAKAO)
                .providerId("12345")
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build());
    }

    /**
     * 테스트용 JWT Access Token 생성
     */
    protected String createAccessToken(User user) {
        return jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name());
    }

    /**
     * Bearer 토큰 헤더 값
     */
    protected String bearerToken(String accessToken) {
        return "Bearer " + accessToken;
    }
}
