package com.pace.server.domain.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pace.server.domain.auth.dto.ReissueRequest;
import com.pace.server.domain.auth.dto.TokenResponse;
import com.pace.server.domain.auth.service.AuthService;
import com.pace.server.global.common.code.SuccessCode;
import com.pace.server.global.common.response.ApiResponse;
import com.pace.server.global.security.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * 인증 API 컨트롤러.
 * 토큰 재발급 및 로그아웃 API 제공.
 */
@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 토큰 재발급 API
     * Refresh Token으로 새로운 Access/Refresh Token 발급 (RTR 적용)
     */
    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새 Access/Refresh Token 발급")
    @PostMapping("/reissue")
    public ApiResponse<TokenResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        TokenResponse response = authService.reissue(request);
        return ApiResponse.success(SuccessCode.TOKEN_REISSUED, response);
    }

    /**
     * 로그아웃 API
     * Redis에서 Refresh Token 삭제
     */
    @Operation(summary = "로그아웃", description = "Refresh Token 삭제")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal CustomOAuth2User principal) {
        authService.logout(principal.getUserId());
        return ApiResponse.success(SuccessCode.LOGOUT_SUCCESS);
    }
}
