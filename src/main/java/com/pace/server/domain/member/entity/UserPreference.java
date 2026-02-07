package com.pace.server.domain.member.entity;

import java.time.LocalTime;

import com.pace.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_preference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserPreference extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pref_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "region_code", length = 10)
	private String regionCode;

	@Column(name = "region_name", length = 50)
	private String regionName;

	@Column(name = "voice_enabled")
	@Builder.Default
	private boolean voiceEnabled = false;

	@Column(name = "target_briefing_time")
	private LocalTime targetBriefingTime;

	// Business Methods
	public void updateRegion(String regionCode, String regionName) {
		this.regionCode = regionCode;
		this.regionName = regionName;
	}

	public void toggleVoice(boolean enabled) {
		this.voiceEnabled = enabled;
	}

	public void updateBriefingTime(LocalTime briefingTime) {
		this.targetBriefingTime = briefingTime;
	}
}
