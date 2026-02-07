package com.pace.server.domain.wake.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pace.server.domain.wake.dto.request.CreateAlarmRequest;
import com.pace.server.domain.wake.dto.request.UpdateAlarmRequest;
import com.pace.server.domain.wake.dto.response.AlarmResponse;
import com.pace.server.domain.wake.service.AlarmService;
import com.pace.server.global.common.code.SuccessCode;
import com.pace.server.global.common.response.ApiResponse;
import com.pace.server.global.security.jwt.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Alarm", description = "알람 API")
@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @Operation(summary = "알람 목록 조회", description = "사용자의 알람 목록을 조회합니다")
    @GetMapping
    public ApiResponse<List<AlarmResponse>> getAlarms(
            @AuthenticationPrincipal JwtAuthentication principal) {
        return ApiResponse.success(SuccessCode.OK, alarmService.getAlarms(principal.userId()));
    }

    @Operation(summary = "알람 생성", description = "새 알람을 생성합니다 (최대 10개)")
    @PostMapping
    public ApiResponse<AlarmResponse> createAlarm(
            @AuthenticationPrincipal JwtAuthentication principal,
            @Valid @RequestBody CreateAlarmRequest request) {
        return ApiResponse.success(SuccessCode.CREATED,
                alarmService.createAlarm(principal.userId(), request));
    }

    @Operation(summary = "알람 수정", description = "알람 설정을 수정합니다")
    @PutMapping("/{id}")
    public ApiResponse<AlarmResponse> updateAlarm(
            @AuthenticationPrincipal JwtAuthentication principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateAlarmRequest request) {
        return ApiResponse.success(SuccessCode.UPDATED,
                alarmService.updateAlarm(principal.userId(), id, request));
    }

    @Operation(summary = "알람 토글", description = "알람 활성화/비활성화 상태를 토글합니다")
    @PatchMapping("/{id}/toggle")
    public ApiResponse<AlarmResponse> toggleAlarm(
            @AuthenticationPrincipal JwtAuthentication principal,
            @PathVariable Long id) {
        return ApiResponse.success(SuccessCode.OK,
                alarmService.toggleAlarm(principal.userId(), id));
    }

    @Operation(summary = "알람 삭제", description = "알람을 삭제합니다")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAlarm(
            @AuthenticationPrincipal JwtAuthentication principal,
            @PathVariable Long id) {
        alarmService.deleteAlarm(principal.userId(), id);
        return ApiResponse.success(SuccessCode.DELETED);
    }
}
