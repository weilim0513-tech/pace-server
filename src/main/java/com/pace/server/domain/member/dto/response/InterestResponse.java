package com.pace.server.domain.member.dto.response;

import com.pace.server.domain.member.entity.InterestItem;

/**
 * 관심 항목 응답 DTO
 */
public record InterestResponse(
	Long itemId,
	String type,
	String value) {
	public static InterestResponse from(InterestItem item) {
		return new InterestResponse(
			item.getId(),
			item.getType().name(),
			item.getValue());
	}
}
