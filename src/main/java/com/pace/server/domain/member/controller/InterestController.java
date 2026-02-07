package com.pace.server.domain.member.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pace.server.domain.member.dto.request.AddInterestRequest;
import com.pace.server.domain.member.dto.response.InterestResponse;
import com.pace.server.domain.member.service.InterestService;
import com.pace.server.global.common.code.SuccessCode;
import com.pace.server.global.common.response.ApiResponse;
import com.pace.server.global.security.oauth2.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 관심 항목 API 컨트롤러.
 * 뉴스 키워드, 주식 종목, 코인 관심사 관리.
 */
@Tag(name = "Interest", description = "관심 항목 API")
@RestController
@RequestMapping("/api/v1/members/me/interests")
@RequiredArgsConstructor
public class InterestController {

	private final InterestService interestService;

	@Operation(summary = "관심 항목 목록 조회")
	@GetMapping
	public ApiResponse<List<InterestResponse>> getInterests(
		@AuthenticationPrincipal CustomOAuth2User principal) {
		List<InterestResponse> response = interestService.getInterests(principal.getUserId());
		return ApiResponse.ok(response);
	}

	@Operation(summary = "관심 항목 추가", description = "키워드/주식/코인 추가 (최대 20개)")
	@PostMapping
	public ApiResponse<InterestResponse> addInterest(
		@AuthenticationPrincipal CustomOAuth2User principal,
		@Valid @RequestBody AddInterestRequest request) {
		InterestResponse response = interestService.addInterest(principal.getUserId(), request);
		return ApiResponse.success(SuccessCode.CREATED, response);
	}

	@Operation(summary = "관심 항목 삭제")
	@DeleteMapping("/{itemId}")
	public ApiResponse<Void> deleteInterest(
		@AuthenticationPrincipal CustomOAuth2User principal,
		@PathVariable Long itemId) {
		interestService.deleteInterest(principal.getUserId(), itemId);
		return ApiResponse.success(SuccessCode.DELETED);
	}
}
