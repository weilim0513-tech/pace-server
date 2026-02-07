package com.pace.server.domain.member.dto.request;

import jakarta.validation.constraints.Size;

/**
 * 프로필 수정 요청 DTO
 */
public record UpdateProfileRequest(
	@Size(min = 2, max = 20, message = "닉네임은 2~20자 이내여야 합니다") String nickname) {
}
