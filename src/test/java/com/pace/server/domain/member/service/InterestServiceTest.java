package com.pace.server.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pace.server.domain.member.dto.request.AddInterestRequest;
import com.pace.server.domain.member.dto.response.InterestResponse;
import com.pace.server.domain.member.entity.InterestItem;
import com.pace.server.domain.member.entity.Provider;
import com.pace.server.domain.member.entity.Role;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.entity.UserStatus;
import com.pace.server.domain.member.repository.InterestItemRepository;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

    @InjectMocks
    private InterestService interestService;

    @Mock
    private InterestItemRepository interestItemRepository;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private InterestItem testInterest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .provider(Provider.KAKAO)
                .providerId("12345")
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        testInterest = InterestItem.builder()
                .id(1L)
                .user(testUser)
                .type(InterestItem.InterestType.KEYWORD)
                .value("AI")
                .build();
    }

    @Nested
    @DisplayName("관심 항목 조회")
    class GetInterests {

        @Test
        @DisplayName("성공: 관심 항목 목록이 조회된다")
        void success() {
            // given
            List<InterestItem> items = List.of(
                    testInterest,
                    InterestItem.builder()
                            .id(2L)
                            .user(testUser)
                            .type(InterestItem.InterestType.STOCK)
                            .value("005930")
                            .build());
            given(interestItemRepository.findByUserId(1L)).willReturn(items);

            // when
            List<InterestResponse> response = interestService.getInterests(1L);

            // then
            assertThat(response).hasSize(2);
            assertThat(response.get(0).value()).isEqualTo("AI");
            assertThat(response.get(1).value()).isEqualTo("005930");
        }

        @Test
        @DisplayName("성공: 관심 항목이 없으면 빈 리스트 반환")
        void success_empty() {
            // given
            given(interestItemRepository.findByUserId(1L)).willReturn(List.of());

            // when
            List<InterestResponse> response = interestService.getInterests(1L);

            // then
            assertThat(response).isEmpty();
        }
    }

    @Nested
    @DisplayName("관심 항목 추가")
    class AddInterest {

        @Test
        @DisplayName("성공: 관심 항목이 추가된다")
        void success() {
            // given
            AddInterestRequest request = new AddInterestRequest(
                    InterestItem.InterestType.KEYWORD, "블록체인");
            given(interestItemRepository.findByUserId(1L)).willReturn(List.of());
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(interestItemRepository.save(any(InterestItem.class))).willAnswer(invocation -> {
                InterestItem item = invocation.getArgument(0);
                return InterestItem.builder()
                        .id(1L)
                        .user(item.getUser())
                        .type(item.getType())
                        .value(item.getValue())
                        .build();
            });

            // when
            InterestResponse response = interestService.addInterest(1L, request);

            // then
            assertThat(response.type()).isEqualTo("KEYWORD");
            assertThat(response.value()).isEqualTo("블록체인");
        }

        @Test
        @DisplayName("실패: 최대 개수 초과")
        void fail_exceedLimit() {
            // given - 20개의 기존 항목
            List<InterestItem> existingItems = java.util.stream.IntStream.range(0, 20)
                    .mapToObj(i -> InterestItem.builder().id((long) i).build())
                    .toList();

            AddInterestRequest request = new AddInterestRequest(
                    InterestItem.InterestType.KEYWORD, "새키워드");
            given(interestItemRepository.findByUserId(1L)).willReturn(existingItems);

            // when & then
            assertThatThrownBy(() -> interestService.addInterest(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("관심 항목 삭제")
    class DeleteInterest {

        @Test
        @DisplayName("성공: 관심 항목이 삭제된다")
        void success() {
            // given
            given(interestItemRepository.findById(1L)).willReturn(Optional.of(testInterest));

            // when
            interestService.deleteInterest(1L, 1L);

            // then
            verify(interestItemRepository).delete(testInterest);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 항목")
        void fail_notFound() {
            // given
            given(interestItemRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> interestService.deleteInterest(1L, 999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 다른 사용자의 항목")
        void fail_accessDenied() {
            // given
            User otherUser = User.builder().id(2L).build();
            InterestItem otherUserItem = InterestItem.builder()
                    .id(2L)
                    .user(otherUser)
                    .type(InterestItem.InterestType.KEYWORD)
                    .value("other")
                    .build();
            given(interestItemRepository.findById(2L)).willReturn(Optional.of(otherUserItem));

            // when & then
            assertThatThrownBy(() -> interestService.deleteInterest(1L, 2L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
        }
    }
}
