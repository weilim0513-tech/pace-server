package com.pace.server.domain.recap.dto;

import lombok.Builder;

/**
 * 뉴스 정보 요약 DTO
 */
@Builder
public record NewsSummary(
        String title,
        String summary,
        String link,
        String category) {
    public static NewsSummary of(String title, String summary) {
        return NewsSummary.builder()
                .title(title)
                .summary(summary)
                .build();
    }
}
