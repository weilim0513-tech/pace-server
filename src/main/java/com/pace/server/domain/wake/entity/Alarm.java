package com.pace.server.domain.wake.entity;

import java.time.LocalTime;

import com.pace.server.domain.member.entity.User;
import com.pace.server.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alarm", indexes = {
	@Index(name = "idx_alarm_user", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Alarm extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alarm_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "wake_time", nullable = false)
	private LocalTime wakeTime;

	@Column(name = "repeat_days", nullable = false, length = 7)
	private String repeatDays; // "0111110" = 월~금

	@Enumerated(EnumType.STRING)
	@Column(name = "mission_type", nullable = false, length = 20)
	@Builder.Default
	private MissionType missionType = MissionType.MATH;

	@Column(name = "mission_level")
	@Builder.Default
	private int missionLevel = 1; // 1~5

	@Column(name = "is_active")
	@Builder.Default
	private boolean isActive = true;

	@Column(length = 50)
	private String label;

	// Business Methods
	public void updateSettings(LocalTime wakeTime, String repeatDays,
		MissionType missionType, int missionLevel) {
		this.wakeTime = wakeTime;
		this.repeatDays = repeatDays;
		this.missionType = missionType;
		this.missionLevel = missionLevel;
	}

	public void toggle() {
		this.isActive = !this.isActive;
	}

	public void updateLabel(String label) {
		this.label = label;
	}
}
