package com.pace.server.domain.recap.dto;

import lombok.Builder;

/**
 * 주식/금융 정보 요약 DTO
 */
@Builder
public record StockSummary(
        String code,
        String name,
        double currentPrice,
        double changeAmount,
        double changePercent,
        String changeSign // 상승/하락/보합
) {
    public static StockSummary empty(String code) {
        return StockSummary.builder()
                .code(code)
                .name("정보 없음")
                .currentPrice(0)
                .changeAmount(0)
                .changePercent(0)
                .changeSign("보합")
                .build();
    }
}
