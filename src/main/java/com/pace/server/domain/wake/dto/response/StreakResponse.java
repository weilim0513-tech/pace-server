package com.pace.server.domain.wake.dto.response;

public record StreakResponse(
        int currentStreak,
        int maxStreak,
        long totalSuccessCount) {
}
