package com.pace.server.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pace.server.domain.member.entity.Provider;
import com.pace.server.domain.member.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

	boolean existsByEmail(String email);
}
