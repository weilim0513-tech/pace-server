package com.pace.server.domain.wake.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.domain.wake.dto.request.CreateAlarmRequest;
import com.pace.server.domain.wake.dto.request.UpdateAlarmRequest;
import com.pace.server.domain.wake.dto.response.AlarmResponse;
import com.pace.server.domain.wake.entity.Alarm;
import com.pace.server.domain.wake.repository.AlarmRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

    private static final int MAX_ALARM_COUNT = 10;

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    public List<AlarmResponse> getAlarms(Long userId) {
        return alarmRepository.findByUserIdOrderByWakeTimeAsc(userId)
                .stream()
                .map(AlarmResponse::from)
                .toList();
    }

    @Transactional
    public AlarmResponse createAlarm(Long userId, CreateAlarmRequest request) {
        // 알람 개수 제한 확인
        if (alarmRepository.countByUserId(userId) >= MAX_ALARM_COUNT) {
            throw new BusinessException(ErrorCode.ALARM_LIMIT_EXCEEDED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Alarm alarm = Alarm.builder()
                .user(user)
                .wakeTime(request.wakeTime())
                .repeatDays(request.repeatDays())
                .missionType(request.missionType())
                .missionLevel(request.missionLevel())
                .label(request.label())
                .isActive(true)
                .build();

        Alarm saved = alarmRepository.save(alarm);
        log.info("Alarm created: userId={}, alarmId={}", userId, saved.getId());

        return AlarmResponse.from(saved);
    }

    @Transactional
    public AlarmResponse updateAlarm(Long userId, Long alarmId, UpdateAlarmRequest request) {
        Alarm alarm = findAlarmByIdAndUserId(alarmId, userId);

        alarm.updateSettings(
                request.wakeTime(),
                request.repeatDays(),
                request.missionType(),
                request.missionLevel());

        if (request.label() != null) {
            alarm.updateLabel(request.label());
        }

        return AlarmResponse.from(alarm);
    }

    @Transactional
    public AlarmResponse toggleAlarm(Long userId, Long alarmId) {
        Alarm alarm = findAlarmByIdAndUserId(alarmId, userId);
        alarm.toggle();
        return AlarmResponse.from(alarm);
    }

    @Transactional
    public void deleteAlarm(Long userId, Long alarmId) {
        Alarm alarm = findAlarmByIdAndUserId(alarmId, userId);
        alarmRepository.delete(alarm);
        log.info("Alarm deleted: userId={}, alarmId={}", userId, alarmId);
    }

    private Alarm findAlarmByIdAndUserId(Long alarmId, Long userId) {
        return alarmRepository.findById(alarmId)
                .filter(a -> a.getUser().getId().equals(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.ALARM_NOT_FOUND));
    }
}
