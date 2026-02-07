package com.pace.server.domain.wake.dto.response;

import java.time.LocalTime;

import com.pace.server.domain.wake.entity.Alarm;
import com.pace.server.domain.wake.entity.MissionType;

public record AlarmResponse(
        Long id,
        LocalTime wakeTime,
        String repeatDays,
        MissionType missionType,
        int missionLevel,
        boolean isActive,
        String label) {
    public static AlarmResponse from(Alarm alarm) {
        return new AlarmResponse(
                alarm.getId(),
                alarm.getWakeTime(),
                alarm.getRepeatDays(),
                alarm.getMissionType(),
                alarm.getMissionLevel(),
                alarm.isActive(),
                alarm.getLabel());
    }
}
