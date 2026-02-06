package com.pace.server.global.security.oauth2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CustomOAuth2User implements OAuth2User {

	private final Long userId;
	private final String email;
	private final String nickname;
	private final String provider;
	private final String providerId;
	private final String role;
	private final Map<String, Object> attributes;
	private final boolean isNewUser;

	@Builder
	public CustomOAuth2User(Long userId, String email, String nickname,
		String provider, String providerId, String role,
		Map<String, Object> attributes, boolean isNewUser) {
		this.userId = userId;
		this.email = email;
		this.nickname = nickname;
		this.provider = provider;
		this.providerId = providerId;
		this.role = role;
		this.attributes = attributes;
		this.isNewUser = isNewUser;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role));
	}

	@Override
	public String getName() {
		return String.valueOf(userId);
	}
}
