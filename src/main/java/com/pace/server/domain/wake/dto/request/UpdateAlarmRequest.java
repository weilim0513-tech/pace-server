package com.pace.server.domain.wake.dto.request;

import java.time.LocalTime;

import com.pace.server.domain.wake.entity.MissionType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAlarmRequest(
        @NotNull(message = "기상 시간은 필수입니다") LocalTime wakeTime,

        @NotNull(message = "반복 요일은 필수입니다") @Pattern(regexp = "^[01]{7}$", message = "반복 요일은 7자리 0/1 문자열이어야 합니다") String repeatDays,

        @NotNull(message = "미션 타입은 필수입니다") MissionType missionType,

        @Min(value = 1, message = "미션 레벨은 1 이상이어야 합니다") @Max(value = 5, message = "미션 레벨은 5 이하이어야 합니다") int missionLevel,

        @Size(max = 50, message = "라벨은 최대 50자까지 가능합니다") String label) {
}
