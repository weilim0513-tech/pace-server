package com.pace.server.domain.wake.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pace.server.domain.wake.dto.request.CreateTodoRequest;
import com.pace.server.domain.wake.dto.response.TodoResponse;
import com.pace.server.domain.wake.service.TodoService;
import com.pace.server.global.common.code.SuccessCode;
import com.pace.server.global.common.response.ApiResponse;
import com.pace.server.global.security.jwt.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Todo", description = "투두 API")
@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @Operation(summary = "오늘 투두 목록 조회", description = "오늘 날짜의 투두 목록을 조회합니다")
    @GetMapping
    public ApiResponse<List<TodoResponse>> getTodayTodos(
            @AuthenticationPrincipal JwtAuthentication principal) {
        return ApiResponse.success(SuccessCode.OK, todoService.getTodayTodos(principal.userId()));
    }

    @Operation(summary = "투두 추가", description = "오늘 날짜에 새 투두를 추가합니다")
    @PostMapping
    public ApiResponse<TodoResponse> createTodo(
            @AuthenticationPrincipal JwtAuthentication principal,
            @Valid @RequestBody CreateTodoRequest request) {
        return ApiResponse.success(SuccessCode.CREATED,
                todoService.createTodo(principal.userId(), request));
    }

    @Operation(summary = "투두 완료 토글", description = "투두의 완료 상태를 토글합니다")
    @PatchMapping("/{id}/toggle")
    public ApiResponse<TodoResponse> toggleTodo(
            @AuthenticationPrincipal JwtAuthentication principal,
            @PathVariable Long id) {
        return ApiResponse.success(SuccessCode.OK,
                todoService.toggleTodo(principal.userId(), id));
    }

    @Operation(summary = "투두 다음날로 미루기", description = "투두를 다음 날로 미룹니다")
    @PostMapping("/{id}/postpone")
    public ApiResponse<TodoResponse> postponeTodo(
            @AuthenticationPrincipal JwtAuthentication principal,
            @PathVariable Long id) {
        return ApiResponse.success(SuccessCode.OK,
                todoService.postponeTodo(principal.userId(), id));
    }

    @Operation(summary = "투두 삭제", description = "투두를 삭제합니다")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTodo(
            @AuthenticationPrincipal JwtAuthentication principal,
            @PathVariable Long id) {
        todoService.deleteTodo(principal.userId(), id);
        return ApiResponse.success(SuccessCode.DELETED);
    }
}
