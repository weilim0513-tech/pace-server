package com.pace.server.domain.member.entity;

import java.time.LocalDateTime;

import com.pace.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", indexes = {
	@Index(name = "idx_users_email", columnList = "email"),
	@Index(name = "idx_users_provider", columnList = "provider, provider_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(length = 50)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Provider provider;

	@Column(name = "provider_id", nullable = false, length = 100)
	private String providerId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private Role role = Role.ROLE_USER;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private UserStatus status = UserStatus.ACTIVE;

	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	// Business Methods
	public void updateLastLoginAt(LocalDateTime loginAt) {
		this.lastLoginAt = loginAt;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void deactivate() {
		this.status = UserStatus.INACTIVE;
	}

	public boolean isActive() {
		return this.status == UserStatus.ACTIVE;
	}
}
