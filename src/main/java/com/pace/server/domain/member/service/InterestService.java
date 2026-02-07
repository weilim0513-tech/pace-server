package com.pace.server.domain.member.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pace.server.domain.member.dto.request.AddInterestRequest;
import com.pace.server.domain.member.dto.response.InterestResponse;
import com.pace.server.domain.member.entity.InterestItem;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.repository.InterestItemRepository;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 관심 항목 서비스.
 * 뉴스 키워드, 주식 종목, 코인 등 관심사 관리.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterestService {

	private static final int MAX_INTEREST_ITEMS = 20;

	private final InterestItemRepository interestItemRepository;
	private final UserRepository userRepository;

	/**
	 * 관심 항목 목록 조회
	 */
	public List<InterestResponse> getInterests(Long userId) {
		return interestItemRepository.findByUserId(userId)
			.stream()
			.map(InterestResponse::from)
			.toList();
	}

	/**
	 * 관심 항목 추가
	 */
	@Transactional
	public InterestResponse addInterest(Long userId, AddInterestRequest request) {
		// 개수 제한 체크
		long currentCount = interestItemRepository.findByUserId(userId).size();
		if (currentCount >= MAX_INTEREST_ITEMS) {
			throw new BusinessException(ErrorCode.BAD_REQUEST, "관심 항목은 최대 " + MAX_INTEREST_ITEMS + "개까지 등록할 수 있습니다.");
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		InterestItem item = InterestItem.builder()
			.user(user)
			.type(request.type())
			.value(request.value())
			.build();

		InterestItem saved = interestItemRepository.save(item);
		log.info("Interest added for user {}: {} - {}", userId, request.type(), request.value());

		return InterestResponse.from(saved);
	}

	/**
	 * 관심 항목 삭제
	 */
	@Transactional
	public void deleteInterest(Long userId, Long itemId) {
		InterestItem item = interestItemRepository.findById(itemId)
			.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

		// 본인 소유 확인
		if (!item.getUser().getId().equals(userId)) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		interestItemRepository.delete(item);
		log.info("Interest deleted for user {}: itemId={}", userId, itemId);
	}
}
