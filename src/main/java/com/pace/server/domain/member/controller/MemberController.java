package com.pace.server.domain.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pace.server.domain.member.dto.request.UpdateProfileRequest;
import com.pace.server.domain.member.dto.response.MemberResponse;
import com.pace.server.domain.member.service.MemberService;
import com.pace.server.global.common.code.SuccessCode;
import com.pace.server.global.common.response.ApiResponse;
import com.pace.server.global.security.jwt.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 회원 API 컨트롤러.
 * 프로필 조회/수정, 회원 탈퇴.
 */
@Tag(name = "Member", description = "회원 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@Operation(summary = "내 프로필 조회")
	@GetMapping("/me")
	public ApiResponse<MemberResponse> getMyProfile(
			@AuthenticationPrincipal JwtAuthentication principal) {
		MemberResponse response = memberService.getProfile(principal.userId());
		return ApiResponse.ok(response);
	}

	@Operation(summary = "프로필 수정", description = "닉네임 변경")
	@PatchMapping("/me")
	public ApiResponse<MemberResponse> updateProfile(
			@AuthenticationPrincipal JwtAuthentication principal,
			@Valid @RequestBody UpdateProfileRequest request) {
		MemberResponse response = memberService.updateProfile(principal.userId(), request);
		return ApiResponse.success(SuccessCode.UPDATED, response);
	}

	@Operation(summary = "회원 탈퇴", description = "계정 비활성화 (Soft Delete)")
	@DeleteMapping("/me")
	public ApiResponse<Void> withdraw(
			@AuthenticationPrincipal JwtAuthentication principal) {
		memberService.withdraw(principal.userId());
		return ApiResponse.success(SuccessCode.DELETED);
	}
}
