package com.pace.server.domain.wake.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.domain.wake.dto.request.WakeLogRequest;
import com.pace.server.domain.wake.dto.response.StreakResponse;
import com.pace.server.domain.wake.dto.response.WakeLogResponse;
import com.pace.server.domain.wake.entity.WakeLog;
import com.pace.server.domain.wake.repository.WakeLogRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WakeLogService {

    private static final int MIN_MISSION_DURATION = 1; // 최소 1초

    private final WakeLogRepository wakeLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public WakeLogResponse recordWakeLog(Long userId, WakeLogRequest request) {
        // 어뷰징 방지: 미션 시간이 너무 짧은 경우
        if (request.durationSeconds() < MIN_MISSION_DURATION) {
            throw new BusinessException(ErrorCode.MISSION_TOO_FAST);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        WakeLog wakeLog = WakeLog.builder()
                .user(user)
                .wakeDate(LocalDate.now())
                .wakeTime(request.wakeTime())
                .isSuccess(request.isSuccess())
                .durationSeconds(request.durationSeconds())
                .build();

        WakeLog saved = wakeLogRepository.save(wakeLog);
        log.info("Wake log recorded: userId={}, success={}", userId, request.isSuccess());

        return WakeLogResponse.from(saved);
    }

    public StreakResponse getStreak(Long userId) {
        List<WakeLog> recentLogs = wakeLogRepository
                .findByUserIdAndIsSuccessTrueOrderByWakeDateDesc(userId);

        int currentStreak = calculateStreak(recentLogs);
        int maxStreak = calculateMaxStreak(recentLogs);
        long totalSuccessCount = wakeLogRepository.countByUserIdAndIsSuccessTrue(userId);

        return new StreakResponse(currentStreak, maxStreak, totalSuccessCount);
    }

    private int calculateStreak(List<WakeLog> logs) {
        if (logs.isEmpty()) {
            return 0;
        }

        int streak = 0;
        LocalDate expected = LocalDate.now();

        for (WakeLog wakeLog : logs) {
            LocalDate logDate = wakeLog.getWakeDate();
            // 오늘 또는 어제부터 연속으로 카운트
            if (logDate.equals(expected) || logDate.equals(expected.minusDays(1))) {
                streak++;
                expected = logDate.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    private int calculateMaxStreak(List<WakeLog> logs) {
        if (logs.isEmpty()) {
            return 0;
        }

        int maxStreak = 0;
        int currentStreak = 1;
        LocalDate prevDate = null;

        for (WakeLog wakeLog : logs) {
            LocalDate logDate = wakeLog.getWakeDate();
            if (prevDate != null) {
                if (prevDate.minusDays(1).equals(logDate)) {
                    currentStreak++;
                } else {
                    maxStreak = Math.max(maxStreak, currentStreak);
                    currentStreak = 1;
                }
            }
            prevDate = logDate;
        }

        return Math.max(maxStreak, currentStreak);
    }
}
