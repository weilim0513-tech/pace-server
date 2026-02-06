package com.pace.server.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.common.code.SuccessCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private LocalDateTime timestamp;
    private int status;
    private String code;
    private String message;
    private T data;

    // 성공 응답 (데이터 포함)
    public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
        return new ApiResponse<>(
                LocalDateTime.now(),
                successCode.getHttpStatus().value(),
                successCode.getCode(),
                successCode.getMessage(),
                data);
    }

    // 성공 응답 (데이터 없음)
    public static ApiResponse<Void> success(SuccessCode successCode) {
        return new ApiResponse<>(
                LocalDateTime.now(),
                successCode.getHttpStatus().value(),
                successCode.getCode(),
                successCode.getMessage(),
                null);
    }

    // 성공 응답 (기본)
    public static <T> ApiResponse<T> ok(T data) {
        return success(SuccessCode.OK, data);
    }

    // 생성 응답
    public static <T> ApiResponse<T> created(T data) {
        return success(SuccessCode.CREATED, data);
    }

    // 에러 응답
    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(
                LocalDateTime.now(),
                errorCode.getHttpStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage(),
                null);
    }

    // 에러 응답 (커스텀 메시지)
    public static ApiResponse<Void> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(
                LocalDateTime.now(),
                errorCode.getHttpStatus().value(),
                errorCode.getCode(),
                message,
                null);
    }

    // 에러 응답 (코드와 메시지 직접 지정)
    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(
                LocalDateTime.now(),
                400,
                code,
                message,
                null);
    }
}
