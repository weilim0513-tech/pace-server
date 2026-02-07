package com.pace.server.domain.wake.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pace.server.domain.wake.dto.request.WakeLogRequest;
import com.pace.server.domain.wake.dto.response.StreakResponse;
import com.pace.server.domain.wake.dto.response.WakeLogResponse;
import com.pace.server.domain.wake.service.WakeLogService;
import com.pace.server.global.common.code.SuccessCode;
import com.pace.server.global.common.response.ApiResponse;
import com.pace.server.global.security.jwt.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Wake", description = "기상 기록 API")
@RestController
@RequestMapping("/api/v1/wake")
@RequiredArgsConstructor
public class WakeController {

    private final WakeLogService wakeLogService;

    @Operation(summary = "기상 로그 기록", description = "미션 완료 후 기상 로그를 기록합니다")
    @PostMapping("/log")
    public ApiResponse<WakeLogResponse> recordWakeLog(
            @AuthenticationPrincipal JwtAuthentication principal,
            @Valid @RequestBody WakeLogRequest request) {
        return ApiResponse.success(SuccessCode.CREATED,
                wakeLogService.recordWakeLog(principal.userId(), request));
    }

    @Operation(summary = "연속 기상 스트릭 조회", description = "연속 기상 일수 및 통계를 조회합니다")
    @GetMapping("/streak")
    public ApiResponse<StreakResponse> getStreak(
            @AuthenticationPrincipal JwtAuthentication principal) {
        return ApiResponse.success(SuccessCode.OK,
                wakeLogService.getStreak(principal.userId()));
    }
}
