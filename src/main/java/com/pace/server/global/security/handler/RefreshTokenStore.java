package com.pace.server.global.security.handler;

/**
 * Refresh Token 저장소 인터페이스.
 * Redis 구현체에서 구현합니다.
 */
public interface RefreshTokenStore {

	/**
	 * Refresh Token을 저장합니다.
	 *
	 * @param userId       사용자 ID
	 * @param refreshToken Refresh Token
	 * @param expiryMillis 만료 시간 (밀리초)
	 */
	void save(Long userId, String refreshToken, long expiryMillis);

	/**
	 * 저장된 Refresh Token을 조회합니다.
	 *
	 * @param userId 사용자 ID
	 * @return 저장된 Refresh Token (없으면 null)
	 */
	String get(Long userId);

	/**
	 * Refresh Token을 삭제합니다.
	 *
	 * @param userId 사용자 ID
	 */
	void delete(Long userId);

	/**
	 * 저장된 Refresh Token과 일치하는지 확인합니다.
	 *
	 * @param userId       사용자 ID
	 * @param refreshToken 검증할 Refresh Token
	 * @return 일치 여부
	 */
	boolean matches(Long userId, String refreshToken);
}
