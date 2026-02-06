package com.pace.server.domain.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pace.server.domain.member.entity.InterestItem;

public interface InterestItemRepository extends JpaRepository<InterestItem, Long> {
	List<InterestItem> findByUserId(Long userId);

	List<InterestItem> findByUserIdAndType(Long userId, InterestItem.InterestType type);

	void deleteByUserIdAndType(Long userId, InterestItem.InterestType type);
}
