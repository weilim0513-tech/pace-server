package com.pace.server.domain.wake.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pace.server.domain.wake.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
	List<Todo> findByUserIdAndTargetDateOrderByDisplayOrderAsc(Long userId, LocalDate targetDate);

	List<Todo> findByUserIdAndTargetDateAndIsDoneFalse(Long userId, LocalDate targetDate);

	long countByUserIdAndTargetDate(Long userId, LocalDate targetDate);
}
