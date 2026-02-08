package com.pace.server.domain.recap.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

/**
 * 모닝 리캡 API 응답 DTO
 */
@Builder
public record RecapResponse(
        LocalDate date,
        WeatherSummary weather,
        List<NewsSummary> news,
        List<StockSummary> finance,
        List<String> todos,
        String motivationMessage) {
    public static RecapResponse empty(LocalDate date) {
        return RecapResponse.builder()
                .date(date)
                .weather(WeatherSummary.empty())
                .news(List.of())
                .finance(List.of())
                .todos(List.of())
                .motivationMessage("🌟 오늘도 화이팅!")
                .build();
    }
}
