package com.pace.server.domain.recap.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
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

import com.pace.server.domain.member.entity.Provider;
import com.pace.server.domain.member.entity.Role;
import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.entity.UserStatus;
import com.pace.server.domain.recap.cache.RecapCacheRepository;
import com.pace.server.domain.recap.dto.RecapResponse;
import com.pace.server.domain.wake.entity.Todo;
import com.pace.server.domain.wake.repository.TodoRepository;

@ExtendWith(MockitoExtension.class)
class RecapServiceTest {

    @InjectMocks
    private RecapService recapService;

    @Mock
    private RecapCacheRepository cacheRepository;

    @Mock
    private TodoRepository todoRepository;

    private User testUser;
    private Todo testTodo;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();

        testUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("테스터")
                .provider(Provider.KAKAO)
                .providerId("12345")
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        testTodo = Todo.builder()
                .id(1L)
                .user(testUser)
                .content("테스트 할일")
                .targetDate(today)
                .isDone(false)
                .build();
    }

    @Nested
    @DisplayName("모닝 리캡 조회")
    class GetRecap {

        @Test
        @DisplayName("성공: 캐시에서 조회된다 (Cache Hit)")
        void success_cacheHit() {
            // given
            RecapResponse cachedResponse = RecapResponse.builder()
                    .date(today)
                    .todos(List.of("캐시된 할일"))
                    .motivationMessage("💪 화이팅!")
                    .build();

            given(cacheRepository.findByUserIdAndDate(1L, today))
                    .willReturn(Optional.of(cachedResponse));

            // when
            RecapResponse result = recapService.getRecap(1L);

            // then
            assertThat(result.date()).isEqualTo(today);
            assertThat(result.todos()).containsExactly("캐시된 할일");
            then(todoRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("성공: 캐시 미스 시 On-demand Fetch")
        void success_cacheMiss() {
            // given
            given(cacheRepository.findByUserIdAndDate(1L, today))
                    .willReturn(Optional.empty());
            given(todoRepository.findByUserIdAndTargetDateAndIsDoneFalse(1L, today))
                    .willReturn(List.of(testTodo));

            // when
            RecapResponse result = recapService.getRecap(1L);

            // then
            assertThat(result.date()).isEqualTo(today);
            assertThat(result.todos()).containsExactly("테스트 할일");
            assertThat(result.weather()).isNotNull();
            assertThat(result.motivationMessage()).isNotNull();

            // 캐시에 저장 확인
            then(cacheRepository).should().save(eq(1L), eq(today), any(RecapResponse.class));
        }

        @Test
        @DisplayName("성공: 투두가 없으면 빈 목록 반환")
        void success_emptyTodos() {
            // given
            given(cacheRepository.findByUserIdAndDate(1L, today))
                    .willReturn(Optional.empty());
            given(todoRepository.findByUserIdAndTargetDateAndIsDoneFalse(1L, today))
                    .willReturn(List.of());

            // when
            RecapResponse result = recapService.getRecap(1L);

            // then
            assertThat(result.todos()).isEmpty();
        }
    }
}
