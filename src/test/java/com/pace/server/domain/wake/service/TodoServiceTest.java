package com.pace.server.domain.wake.service;

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
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.domain.wake.dto.request.CreateTodoRequest;
import com.pace.server.domain.wake.dto.response.TodoResponse;
import com.pace.server.domain.wake.entity.Todo;
import com.pace.server.domain.wake.repository.TodoRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private Todo testTodo;

    @BeforeEach
    void setUp() {
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
                .content("운동하기")
                .targetDate(LocalDate.now())
                .isDone(false)
                .displayOrder(0)
                .build();
    }

    @Nested
    @DisplayName("오늘 투두 목록 조회")
    class GetTodayTodos {

        @Test
        @DisplayName("성공: 오늘 투두 목록이 조회된다")
        void success() {
            // given
            given(todoRepository.findByUserIdAndTargetDateOrderByDisplayOrderAsc(eq(1L), any(LocalDate.class)))
                    .willReturn(List.of(testTodo));

            // when
            List<TodoResponse> result = todoService.getTodayTodos(1L);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).content()).isEqualTo("운동하기");
        }

        @Test
        @DisplayName("성공: 투두가 없으면 빈 목록 반환")
        void success_empty() {
            // given
            given(todoRepository.findByUserIdAndTargetDateOrderByDisplayOrderAsc(eq(1L), any(LocalDate.class)))
                    .willReturn(List.of());

            // when
            List<TodoResponse> result = todoService.getTodayTodos(1L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("투두 생성")
    class CreateTodo {

        @Test
        @DisplayName("성공: 투두가 정상적으로 생성된다")
        void success() {
            // given
            CreateTodoRequest request = new CreateTodoRequest("새 투두");
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
            given(todoRepository.countByUserIdAndTargetDate(eq(1L), any(LocalDate.class))).willReturn(0L);
            given(todoRepository.save(any(Todo.class))).willAnswer(invocation -> {
                Todo todo = invocation.getArgument(0);
                return Todo.builder()
                        .id(2L)
                        .user(todo.getUser())
                        .content(todo.getContent())
                        .targetDate(todo.getTargetDate())
                        .isDone(false)
                        .displayOrder(0)
                        .build();
            });

            // when
            TodoResponse result = todoService.createTodo(1L, request);

            // then
            assertThat(result.content()).isEqualTo("새 투두");
            assertThat(result.isDone()).isFalse();
            then(todoRepository).should().save(any(Todo.class));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자")
        void fail_userNotFound() {
            // given
            CreateTodoRequest request = new CreateTodoRequest("새 투두");
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> todoService.createTodo(999L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("투두 완료 토글")
    class ToggleTodo {

        @Test
        @DisplayName("성공: 투두가 완료 상태로 변경된다")
        void success_markDone() {
            // given
            given(todoRepository.findById(1L)).willReturn(Optional.of(testTodo));

            // when
            TodoResponse result = todoService.toggleTodo(1L, 1L);

            // then
            assertThat(result.isDone()).isTrue();
        }

        @Test
        @DisplayName("실패: 다른 사용자의 투두")
        void fail_notOwner() {
            // given
            User anotherUser = User.builder().id(2L).build();
            Todo anotherTodo = Todo.builder().id(2L).user(anotherUser).build();
            given(todoRepository.findById(2L)).willReturn(Optional.of(anotherTodo));

            // when & then
            assertThatThrownBy(() -> todoService.toggleTodo(1L, 2L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("투두 미루기")
    class PostponeTodo {

        @Test
        @DisplayName("성공: 투두가 다음날로 미뤄진다")
        void success() {
            // given
            given(todoRepository.findById(1L)).willReturn(Optional.of(testTodo));
            given(todoRepository.save(any(Todo.class))).willAnswer(invocation -> {
                Todo todo = invocation.getArgument(0);
                return Todo.builder()
                        .id(2L)
                        .user(todo.getUser())
                        .content(todo.getContent())
                        .targetDate(todo.getTargetDate())
                        .isDone(false)
                        .build();
            });

            // when
            TodoResponse result = todoService.postponeTodo(1L, 1L);

            // then
            assertThat(result.targetDate()).isEqualTo(LocalDate.now().plusDays(1));
            then(todoRepository).should().delete(testTodo);
            then(todoRepository).should().save(any(Todo.class));
        }
    }

    @Nested
    @DisplayName("투두 삭제")
    class DeleteTodo {

        @Test
        @DisplayName("성공: 투두가 정상적으로 삭제된다")
        void success() {
            // given
            given(todoRepository.findById(1L)).willReturn(Optional.of(testTodo));

            // when
            todoService.deleteTodo(1L, 1L);

            // then
            then(todoRepository).should().delete(testTodo);
        }
    }
}
