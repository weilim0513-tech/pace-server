package com.pace.server.domain.member.dto.response;

import java.time.LocalTime;

import com.pace.server.domain.member.entity.UserPreference;

/**
 * 사용자 설정 응답 DTO
 */
public record PreferenceResponse(
        Long prefId,
        String regionCode,
        String regionName,
        boolean voiceEnabled,
        LocalTime targetBriefingTime) {
    public static PreferenceResponse from(UserPreference pref) {
        return new PreferenceResponse(
                pref.getId(),
                pref.getRegionCode(),
                pref.getRegionName(),
                pref.isVoiceEnabled(),
                pref.getTargetBriefingTime());
    }
}
