package com.pace.server.domain.recap.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pace.server.domain.member.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recap_history", indexes = {
	@Index(name = "idx_recap_user_date", columnList = "user_id, recap_date DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecapHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recap_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "recap_date", nullable = false)
	private LocalDate recapDate;

	@Column(name = "weather_summary", length = 500)
	private String weatherSummary;

	@Column(name = "news_summary", length = 2000)
	private String newsSummary;

	@Column(name = "schedule_summary", length = 1000)
	private String scheduleSummary;

	@Column(name = "todo_summary", length = 1000)
	private String todoSummary;

	@Column(name = "tts_url", length = 500)
	private String ttsUrl;

	@Column(name = "is_read")
	@Builder.Default
	private boolean isRead = false;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	// Business Methods
	public void markAsRead() {
		this.isRead = true;
	}
}
