package com.pace.server.global.security.oauth2;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.pace.server.global.error.exception.OAuth2AuthenticationProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final OAuth2UserProcessor oauth2UserProcessor;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oauth2User = super.loadUser(userRequest);

		try {
			return processOAuth2User(userRequest, oauth2User);
		} catch (OAuth2AuthenticationProcessingException e) {
			throw e;
		} catch (Exception e) {
			log.error("OAuth2 인증 처리 중 오류 발생", e);
			throw new OAuth2AuthenticationProcessingException("OAuth2 인증 처리 중 오류가 발생했습니다.", e);
		}
	}

	private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2UserInfo userInfo = OAuth2UserInfo.of(registrationId, oauth2User.getAttributes());

		if (userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
			throw new OAuth2AuthenticationProcessingException("이메일 정보를 가져올 수 없습니다.");
		}

		return oauth2UserProcessor.processUser(registrationId, userInfo, oauth2User.getAttributes());
	}
}
