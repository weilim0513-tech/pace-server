package com.pace.server.domain.recap.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pace.server.domain.recap.dto.RecapResponse;
import com.pace.server.domain.recap.service.RecapService;
import com.pace.server.global.common.response.ApiResponse;
import com.pace.server.global.security.jwt.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Recap", description = "모닝 리캡 API")
@RestController
@RequestMapping("/api/v1/recap")
@RequiredArgsConstructor
public class RecapController {

    private final RecapService recapService;

    @Operation(summary = "오늘의 모닝 리캡 조회", description = "오늘의 날씨, 뉴스, 금융, 투두 정보를 조합하여 리캡 데이터를 반환합니다.")
    @GetMapping
    public ApiResponse<RecapResponse> getRecap(
            @AuthenticationPrincipal JwtAuthentication principal) {
        RecapResponse response = recapService.getRecap(principal.userId());
        return ApiResponse.ok(response);
    }
}
