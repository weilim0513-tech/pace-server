package com.pace.server.domain.wake.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pace.server.domain.wake.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
	List<Alarm> findByUserIdOrderByWakeTimeAsc(Long userId);

	List<Alarm> findByUserIdAndIsActiveTrue(Long userId);

	long countByUserId(Long userId);
}
