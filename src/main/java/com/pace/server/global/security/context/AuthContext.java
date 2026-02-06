package com.pace.server.global.security.context;

/**
 * Java 25 Scoped Values를 활용한 인증 컨텍스트.
 * ThreadLocal 대신 Virtual Thread 친화적인 방식으로 인증 정보를 전달합니다.
 *
 * 사용 예:
 * 
 * <pre>
 * ScopedValue.runWhere(AuthContext.CURRENT_USER, authUser, () -> {
 *     AuthUser user = AuthContext.getCurrentUser();
 *     // 비즈니스 로직...
 * });
 * </pre>
 */
public final class AuthContext {

    private AuthContext() {
    }

    /**
     * 현재 인증된 사용자 정보를 담는 Scoped Value.
     */
    public static final ScopedValue<AuthUser> CURRENT_USER = ScopedValue.newInstance();

    /**
     * 현재 인증된 사용자를 반환합니다.
     *
     * @return AuthUser 또는 null (비로그인 상태)
     */
    public static AuthUser getCurrentUser() {
        if (CURRENT_USER.isBound()) {
            return CURRENT_USER.get();
        }
        return null;
    }

    /**
     * 현재 사용자 ID를 반환합니다.
     *
     * @return userId 또는 null
     */
    public static Long getCurrentUserId() {
        AuthUser user = getCurrentUser();
        return user != null ? user.userId() : null;
    }

    /**
     * 인증 필수 컨텍스트에서 현재 사용자를 반환합니다.
     *
     * @return AuthUser (never null)
     * @throws IllegalStateException 비로그인 상태인 경우
     */
    public static AuthUser requireCurrentUser() {
        AuthUser user = getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("Authentication required");
        }
        return user;
    }
}
