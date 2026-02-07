package com.pace.server.domain.member.dto.response;

import com.pace.server.domain.member.entity.User;

/**
 * 회원 정보 응답 DTO
 */
public record MemberResponse(
	Long userId,
	String email,
	String nickname,
	String provider,
	String status) {
	public static MemberResponse from(User user) {
		return new MemberResponse(
			user.getId(),
			user.getEmail(),
			user.getNickname(),
			user.getProvider().name(),
			user.getStatus().name());
	}
}
