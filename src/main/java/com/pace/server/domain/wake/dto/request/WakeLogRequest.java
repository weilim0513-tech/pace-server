package com.pace.server.domain.wake.dto.request;

import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WakeLogRequest(
        @NotNull(message = "기상 시간은 필수입니다") LocalTime wakeTime,

        boolean isSuccess,

        @Min(value = 0, message = "미션 소요 시간은 0 이상이어야 합니다") int durationSeconds) {
}
