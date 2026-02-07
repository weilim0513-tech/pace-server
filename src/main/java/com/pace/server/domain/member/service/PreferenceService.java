package com.pace.server.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pace.server.domain.member.dto.request.UpdatePreferenceRequest;
import com.pace.server.domain.member.dto.response.PreferenceResponse;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.entity.UserPreference;
import com.pace.server.domain.member.repository.UserPreferenceRepository;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 설정 서비스.
 * 지역, 음성 설정, 브리핑 시간 관리.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreferenceService {

	private final UserPreferenceRepository preferenceRepository;
	private final UserRepository userRepository;

	/**
	 * 설정 조회 (없으면 기본값 생성)
	 */
	public PreferenceResponse getPreference(Long userId) {
		UserPreference pref = findOrCreate(userId);
		return PreferenceResponse.from(pref);
	}

	/**
	 * 설정 수정
	 */
	@Transactional
	public PreferenceResponse updatePreference(Long userId, UpdatePreferenceRequest request) {
		UserPreference pref = findOrCreate(userId);

		if (request.regionCode() != null && request.regionName() != null) {
			pref.updateRegion(request.regionCode(), request.regionName());
		}
		if (request.voiceEnabled() != null) {
			pref.toggleVoice(request.voiceEnabled());
		}
		if (request.targetBriefingTime() != null) {
			pref.updateBriefingTime(request.targetBriefingTime());
		}

		log.info("Preference updated for user: {}", userId);
		return PreferenceResponse.from(pref);
	}

	private UserPreference findOrCreate(Long userId) {
		return preferenceRepository.findByUserId(userId)
			.orElseGet(() -> createDefault(userId));
	}

	@Transactional
	protected UserPreference createDefault(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		UserPreference pref = UserPreference.builder()
			.user(user)
			.voiceEnabled(true)
			.build();

		return preferenceRepository.save(pref);
	}
}
