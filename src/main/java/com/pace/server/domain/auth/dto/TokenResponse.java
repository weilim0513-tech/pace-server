package com.pace.server.domain.auth.dto;

/**
 * JWT 토큰 응답 DTO
 */
public record TokenResponse(
        String accessToken,
        String refreshToken) {
}
