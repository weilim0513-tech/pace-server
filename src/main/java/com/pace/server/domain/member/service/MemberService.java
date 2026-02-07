package com.pace.server.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pace.server.domain.member.dto.request.UpdateProfileRequest;
import com.pace.server.domain.member.dto.response.MemberResponse;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회원 서비스.
 * 프로필 조회/수정, 회원 탈퇴 관련 비즈니스 로직.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final UserRepository userRepository;

    /**
     * 내 프로필 조회
     */
    public MemberResponse getProfile(Long userId) {
        User user = findActiveUserById(userId);
        return MemberResponse.from(user);
    }

    /**
     * 프로필 수정 (닉네임)
     */
    @Transactional
    public MemberResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findActiveUserById(userId);

        if (request.nickname() != null) {
            user.updateNickname(request.nickname());
        }

        log.info("Profile updated for user: {}", userId);
        return MemberResponse.from(user);
    }

    /**
     * 회원 탈퇴 (Soft Delete)
     */
    @Transactional
    public void withdraw(Long userId) {
        User user = findActiveUserById(userId);
        user.deactivate();
        log.info("User withdrawn: {}", userId);
    }

    private User findActiveUserById(Long userId) {
        return userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
