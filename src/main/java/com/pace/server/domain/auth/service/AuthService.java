package com.pace.server.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pace.server.domain.auth.dto.ReissueRequest;
import com.pace.server.domain.auth.dto.TokenResponse;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.AuthException;
import com.pace.server.global.security.handler.RefreshTokenStore;
import com.pace.server.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 서비스.
 * 토큰 재발급(RTR) 및 로그아웃 처리.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenStore refreshTokenStore;
	private final UserRepository userRepository;

	/**
	 * 토큰 재발급 (Refresh Token Rotation 적용)
	 *
	 * @param request Refresh Token 요청
	 * @return 새로운 Access/Refresh Token
	 */
	@Transactional
	public TokenResponse reissue(ReissueRequest request) {
		String refreshToken = request.refreshToken();

		// 1. Refresh Token 유효성 검증
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new AuthException(ErrorCode.TOKEN_INVALID);
		}

		// 2. 토큰에서 userId 추출
		Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

		// 3. Redis에 저장된 RT와 비교
		if (!refreshTokenStore.matches(userId, refreshToken)) {
			log.warn("Refresh token mismatch for user: {}", userId);
			throw new AuthException(ErrorCode.REFRESH_TOKEN_MISMATCH);
		}

		// 4. 유저 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

		// 5. 비활성화된 사용자 체크
		if (!user.isActive()) {
			throw new AuthException(ErrorCode.USER_INACTIVE);
		}

		// 6. 새 토큰 발급
		String newAccessToken = jwtTokenProvider.createAccessToken(
			user.getId(), user.getEmail(), user.getRole().name());
		String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());

		// 7. RTR: 기존 RT 삭제 후 새 RT 저장
		refreshTokenStore.delete(userId);
		refreshTokenStore.save(userId, newRefreshToken, jwtTokenProvider.getRefreshTokenExpiry());

		log.info("Token reissued for user: {}", userId);

		return new TokenResponse(newAccessToken, newRefreshToken);
	}

	/**
	 * 로그아웃 - Redis에서 Refresh Token 삭제
	 *
	 * @param userId 사용자 ID
	 */
	@Transactional
	public void logout(Long userId) {
		refreshTokenStore.delete(userId);
		log.info("User logged out: {}", userId);
	}
}
