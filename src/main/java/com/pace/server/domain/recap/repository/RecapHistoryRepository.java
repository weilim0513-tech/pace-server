package com.pace.server.domain.recap.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pace.server.domain.recap.entity.RecapHistory;

public interface RecapHistoryRepository extends JpaRepository<RecapHistory, Long> {
	Optional<RecapHistory> findByUserIdAndRecapDate(Long userId, LocalDate recapDate);

	Optional<RecapHistory> findFirstByUserIdOrderByRecapDateDesc(Long userId);
}
