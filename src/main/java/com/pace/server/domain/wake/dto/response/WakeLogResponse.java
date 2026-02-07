package com.pace.server.domain.wake.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.pace.server.domain.wake.entity.WakeLog;

public record WakeLogResponse(
        Long id,
        LocalDate wakeDate,
        LocalTime wakeTime,
        boolean isSuccess,
        int durationSeconds,
        LocalDateTime createdAt) {
    public static WakeLogResponse from(WakeLog wakeLog) {
        return new WakeLogResponse(
                wakeLog.getId(),
                wakeLog.getWakeDate(),
                wakeLog.getWakeTime(),
                wakeLog.isSuccess(),
                wakeLog.getDurationSeconds(),
                wakeLog.getCreatedAt());
    }
}
