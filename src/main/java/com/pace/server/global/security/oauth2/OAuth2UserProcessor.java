package com.pace.server.global.security.oauth2;

import java.util.Map;

/**
 * OAuth2 사용자 처리 인터페이스.
 * 도메인 레이어(Member Service)에서 구현합니다.
 */
public interface OAuth2UserProcessor {

	/**
	 * OAuth2 사용자 정보를 처리하여 CustomOAuth2User를 반환합니다.
	 * 신규 사용자는 자동 회원가입, 기존 사용자는 로그인 처리됩니다.
	 *
	 * @param registrationId OAuth2 provider (kakao, google)
	 * @param userInfo       소셜 로그인에서 받아온 사용자 정보
	 * @param attributes     원본 OAuth2 attributes
	 * @return CustomOAuth2User
	 */
	CustomOAuth2User processUser(String registrationId, OAuth2UserInfo userInfo, Map<String, Object> attributes);
}
