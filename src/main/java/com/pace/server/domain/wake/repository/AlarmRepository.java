package com.pace.server.domain.wake.repository;

import com.pace.server.domain.wake.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByUserIdOrderByWakeTimeAsc(Long userId);

    List<Alarm> findByUserIdAndIsActiveTrue(Long userId);

    long countByUserId(Long userId);
}
