package com.pace.server.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 재발급 요청 DTO
 */
public record ReissueRequest(
	@NotBlank(message = "Refresh Token은 필수입니다") String refreshToken) {
}
