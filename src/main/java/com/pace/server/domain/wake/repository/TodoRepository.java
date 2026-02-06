package com.pace.server.domain.wake.repository;

import com.pace.server.domain.wake.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserIdAndTargetDateOrderByDisplayOrderAsc(Long userId, LocalDate targetDate);

    List<Todo> findByUserIdAndTargetDateAndIsDoneFalse(Long userId, LocalDate targetDate);

    long countByUserIdAndTargetDate(Long userId, LocalDate targetDate);
}
