package com.pace.server.domain.wake.dto.response;

import java.time.LocalDate;

import com.pace.server.domain.wake.entity.Todo;

public record TodoResponse(
        Long id,
        String content,
        LocalDate targetDate,
        boolean isDone,
        int displayOrder) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getContent(),
                todo.getTargetDate(),
                todo.isDone(),
                todo.getDisplayOrder());
    }
}
