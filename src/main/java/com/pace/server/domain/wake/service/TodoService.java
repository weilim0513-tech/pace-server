package com.pace.server.domain.wake.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pace.server.domain.member.entity.User;
import com.pace.server.domain.member.repository.UserRepository;
import com.pace.server.domain.wake.dto.request.CreateTodoRequest;
import com.pace.server.domain.wake.dto.response.TodoResponse;
import com.pace.server.domain.wake.entity.Todo;
import com.pace.server.domain.wake.repository.TodoRepository;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public List<TodoResponse> getTodayTodos(Long userId) {
        return todoRepository.findByUserIdAndTargetDateOrderByDisplayOrderAsc(userId, LocalDate.now())
                .stream()
                .map(TodoResponse::from)
                .toList();
    }

    @Transactional
    public TodoResponse createTodo(Long userId, CreateTodoRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        int nextOrder = (int) todoRepository.countByUserIdAndTargetDate(userId, LocalDate.now());

        Todo todo = Todo.builder()
                .user(user)
                .content(request.content())
                .targetDate(LocalDate.now())
                .isDone(false)
                .displayOrder(nextOrder)
                .build();

        Todo saved = todoRepository.save(todo);
        log.info("Todo created: userId={}, todoId={}", userId, saved.getId());

        return TodoResponse.from(saved);
    }

    @Transactional
    public TodoResponse toggleTodo(Long userId, Long todoId) {
        Todo todo = findTodoByIdAndUserId(todoId, userId);
        todo.toggleDone();
        return TodoResponse.from(todo);
    }

    @Transactional
    public TodoResponse postponeTodo(Long userId, Long todoId) {
        Todo oldTodo = findTodoByIdAndUserId(todoId, userId);
        Todo newTodo = oldTodo.postponeTo(LocalDate.now().plusDays(1));

        todoRepository.delete(oldTodo);
        Todo saved = todoRepository.save(newTodo);

        log.info("Todo postponed: userId={}, oldTodoId={}, newTodoId={}",
                userId, todoId, saved.getId());

        return TodoResponse.from(saved);
    }

    @Transactional
    public void deleteTodo(Long userId, Long todoId) {
        Todo todo = findTodoByIdAndUserId(todoId, userId);
        todoRepository.delete(todo);
        log.info("Todo deleted: userId={}, todoId={}", userId, todoId);
    }

    private Todo findTodoByIdAndUserId(Long todoId, Long userId) {
        return todoRepository.findById(todoId)
                .filter(t -> t.getUser().getId().equals(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
