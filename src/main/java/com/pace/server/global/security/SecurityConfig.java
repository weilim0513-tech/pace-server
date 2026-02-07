package com.pace.server.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pace.server.global.security.handler.JwtAccessDeniedHandler;
import com.pace.server.global.security.handler.JwtAuthenticationEntryPoint;
import com.pace.server.global.security.handler.OAuth2AuthenticationFailureHandler;
import com.pace.server.global.security.handler.OAuth2AuthenticationSuccessHandler;
import com.pace.server.global.security.jwt.JwtAuthenticationFilter;
import com.pace.server.global.security.oauth2.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	// 인증 없이 접근 가능한 경로
	private static final String[] PUBLIC_PATHS = {
			"/",
			"/health",
			"/error",
			"/favicon.ico"
	};
	// Swagger 관련 경로
	private static final String[] SWAGGER_PATHS = {
			"/swagger-ui.html",
			"/swagger-ui/**",
			"/v3/api-docs",
			"/v3/api-docs/**",
			"/swagger-resources/**",
			"/webjars/**"
	};
	// 인증 관련 경로 (공개)
	private static final String[] AUTH_PATHS = {
			"/api/v1/auth/reissue", // 토큰 재발급만 공개 (logout은 인증 필요)
			"/login/**",
			"/oauth2/**"
	};
	// Actuator 경로
	private static final String[] ACTUATOR_PATHS = {
			"/actuator/**"
	};
	// H2 Console 경로
	private static final String[] H2_PATHS = {
			"/h2-console",
			"/h2-console/**"
	};
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	/**
	 * H2 Console, Swagger 등 정적 리소스는 시큐리티 필터 체인 자체를 우회
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
				.requestMatchers("/h2-console/**")
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
						"/webjars/**");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				// CSRF 비활성화 (JWT 사용)
				.csrf(AbstractHttpConfigurer::disable)

				// 세션 사용 안함 (Stateless)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// H2 Console iframe 허용
				.headers(headers -> headers.frameOptions(frame -> frame.disable()))

				// 경로별 인가 설정
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(PUBLIC_PATHS).permitAll()
						.requestMatchers(SWAGGER_PATHS).permitAll()
						.requestMatchers(AUTH_PATHS).permitAll()
						.requestMatchers(ACTUATOR_PATHS).permitAll()
						.requestMatchers(H2_PATHS).permitAll()
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.anyRequest().authenticated())

				// OAuth2 로그인 설정
				.oauth2Login(oauth2 -> oauth2
						.userInfoEndpoint(userInfo -> userInfo
								.userService(customOAuth2UserService))
						.successHandler(oAuth2AuthenticationSuccessHandler)
						.failureHandler(oAuth2AuthenticationFailureHandler))

				// 예외 핸들러
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(jwtAuthenticationEntryPoint)
						.accessDeniedHandler(jwtAccessDeniedHandler))

				// JWT 필터 추가
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

				.build();
	}
}
