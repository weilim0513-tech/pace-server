package com.pace.server.global.security.context;

/**
 * 인증된 사용자 정보 레코드.
 * Scoped Value에 저장되어 요청 전반에 걸쳐 사용됩니다.
 */
public record AuthUser(
	Long userId,
	String email,
	String nickname,
	String role) {
	public boolean isAdmin() {
		return "ROLE_ADMIN".equals(role);
	}
}
