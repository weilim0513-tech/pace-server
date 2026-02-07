package com.pace.server.domain.member.dto.request;

import java.time.LocalTime;

import jakarta.validation.constraints.Size;

/**
 * 사용자 설정 수정 요청 DTO
 */
public record UpdatePreferenceRequest(
        @Size(max = 10, message = "지역 코드는 10자 이하여야 합니다") String regionCode,

        @Size(max = 50, message = "지역명은 50자 이하여야 합니다") String regionName,

        Boolean voiceEnabled,

        LocalTime targetBriefingTime) {
}
