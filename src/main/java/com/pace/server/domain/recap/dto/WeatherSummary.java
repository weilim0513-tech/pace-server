package com.pace.server.domain.recap.dto;

import lombok.Builder;

/**
 * 날씨 정보 요약 DTO
 */
@Builder
public record WeatherSummary(
        String regionName,
        Double temperature,
        String condition,
        Integer precipitationProbability,
        boolean umbrellaNeeded,
        boolean isFallback,
        String fallbackMessage) {
    public static WeatherSummary empty() {
        return WeatherSummary.builder()
                .isFallback(true)
                .fallbackMessage("날씨 정보를 불러올 수 없습니다.")
                .build();
    }
}
