package com.pace.server.domain.member.repository;

import com.pace.server.domain.member.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    List<UserDevice> findByUserIdAndIsActiveTrue(Long userId);

    Optional<UserDevice> findByDeviceUuid(String deviceUuid);
}
