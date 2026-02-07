package com.pace.server.domain.member.dto.request;

import com.pace.server.domain.member.entity.InterestItem.InterestType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 관심 항목 추가 요청 DTO
 */
public record AddInterestRequest(
        @NotNull(message = "관심 유형은 필수입니다") InterestType type,

        @NotBlank(message = "관심 항목 값은 필수입니다") @Size(max = 100, message = "관심 항목 값은 100자 이하여야 합니다") String value) {
}
