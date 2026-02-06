package com.pace.server.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pace.server.domain.member.entity.UserPreference;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
	Optional<UserPreference> findByUserId(Long userId);
}
