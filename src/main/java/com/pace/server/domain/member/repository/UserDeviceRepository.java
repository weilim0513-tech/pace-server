package com.pace.server.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pace.server.domain.member.entity.UserDevice;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
	List<UserDevice> findByUserIdAndIsActiveTrue(Long userId);

	Optional<UserDevice> findByDeviceUuid(String deviceUuid);
}
