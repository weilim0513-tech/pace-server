package com.pace.server.domain.wake.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTodoRequest(
        @NotBlank(message = "투두 내용은 필수입니다") @Size(max = 200, message = "투두 내용은 최대 200자까지 가능합니다") String content) {
}
