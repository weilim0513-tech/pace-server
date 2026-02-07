package com.pace.server.domain.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pace.server.domain.member.dto.request.UpdatePreferenceRequest;
import com.pace.server.domain.member.dto.response.PreferenceResponse;
import com.pace.server.domain.member.service.PreferenceService;
import com.pace.server.global.common.code.SuccessCode;
import com.pace.server.global.common.response.ApiResponse;
import com.pace.server.global.security.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 설정 API 컨트롤러.
 * 지역, 음성 설정, 브리핑 시간 관리.
 */
@Tag(name = "Preference", description = "사용자 설정 API")
@RestController
@RequestMapping("/api/v1/members/me/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @Operation(summary = "설정 조회")
    @GetMapping
    public ApiResponse<PreferenceResponse> getPreference(
            @AuthenticationPrincipal CustomOAuth2User principal) {
        PreferenceResponse response = preferenceService.getPreference(principal.getUserId());
        return ApiResponse.ok(response);
    }

    @Operation(summary = "설정 수정", description = "지역, 음성, 브리핑 시간 설정")
    @PutMapping
    public ApiResponse<PreferenceResponse> updatePreference(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @Valid @RequestBody UpdatePreferenceRequest request) {
        PreferenceResponse response = preferenceService.updatePreference(principal.getUserId(), request);
        return ApiResponse.success(SuccessCode.UPDATED, response);
    }
}
