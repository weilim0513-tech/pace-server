package com.pace.server.domain.recap.repository;

import com.pace.server.domain.recap.entity.RecapHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RecapHistoryRepository extends JpaRepository<RecapHistory, Long> {
    Optional<RecapHistory> findByUserIdAndRecapDate(Long userId, LocalDate recapDate);

    Optional<RecapHistory> findFirstByUserIdOrderByRecapDateDesc(Long userId);
}
