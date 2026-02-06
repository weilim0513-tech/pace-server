package com.pace.server.domain.wake.entity;

import com.pace.server.domain.member.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "wake_log", indexes = {
        @Index(name = "idx_wakelog_streak", columnList = "user_id, wake_date DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WakeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "wake_date", nullable = false)
    private LocalDate wakeDate;

    @Column(name = "wake_time", nullable = false)
    private LocalTime wakeTime;

    @Column(name = "is_success")
    @Builder.Default
    private boolean isSuccess = true;

    @Column(name = "duration_seconds")
    private int durationSeconds;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 어뷰징 검증 (0.5초 미만)
    public boolean isSuspicious() {
        return this.durationSeconds < 1;
    }
}
