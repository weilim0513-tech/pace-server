package com.pace.server.domain.member.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pace.server.domain.member.entity.Provider;
import com.pace.server.domain.member.entity.Role;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.entity.UserStatus;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.global.security.oauth2.CustomOAuth2User;
import com.pace.server.global.security.oauth2.OAuth2UserInfo;
import com.pace.server.global.security.oauth2.OAuth2UserProcessor;
import com.pace.server.global.util.DateTimeUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserProcessorImpl implements OAuth2UserProcessor {

	private final UserRepository userRepository;

	@Override
	@Transactional
	public CustomOAuth2User processUser(String registrationId, OAuth2UserInfo userInfo,
		Map<String, Object> attributes) {
		Provider provider = Provider.valueOf(registrationId.toUpperCase());

		// 기존 사용자 조회
		User user = userRepository.findByProviderAndProviderId(provider, userInfo.getProviderId())
			.orElse(null);

		boolean isNewUser = false;

		if (user == null) {
			// 신규 사용자 - 회원가입
			user = createUser(provider, userInfo);
			isNewUser = true;
			log.info("신규 사용자 가입 - provider: {}, email: {}", provider, userInfo.getEmail());
		} else {
			// 기존 사용자 - 마지막 로그인 시간 업데이트
			user.updateLastLoginAt(DateTimeUtil.now());
			log.info("기존 사용자 로그인 - userId: {}", user.getId());
		}

		return CustomOAuth2User.builder()
			.userId(user.getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.provider(provider.name())
			.providerId(userInfo.getProviderId())
			.role(user.getRole().name())
			.attributes(attributes)
			.isNewUser(isNewUser)
			.build();
	}

	private User createUser(Provider provider, OAuth2UserInfo userInfo) {
		User user = User.builder()
			.email(userInfo.getEmail())
			.nickname(userInfo.getNickname())
			.provider(provider)
			.providerId(userInfo.getProviderId())
			.role(Role.ROLE_USER)
			.status(UserStatus.ACTIVE)
			.lastLoginAt(DateTimeUtil.now())
			.build();

		return userRepository.save(user);
	}
}
