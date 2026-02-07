package com.pace.server.domain.wake.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pace.server.domain.wake.entity.WakeLog;

public interface WakeLogRepository extends JpaRepository<WakeLog, Long> {
	List<WakeLog> findByUserIdOrderByWakeDateDesc(Long userId);

	Optional<WakeLog> findByUserIdAndWakeDate(Long userId, LocalDate wakeDate);

	List<WakeLog> findByUserIdAndIsSuccessTrueOrderByWakeDateDesc(Long userId);

	long countByUserIdAndIsSuccessTrue(Long userId);

	@Query("SELECT COUNT(w) FROM WakeLog w WHERE w.user.id = :userId AND w.isSuccess = true AND w.wakeDate >= :startDate")
	long countSuccessfulWakes(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);
}
