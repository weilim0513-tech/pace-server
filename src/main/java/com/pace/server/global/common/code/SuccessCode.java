package com.pace.server.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // Common
    OK(HttpStatus.OK, "SUCCESS", "요청이 성공했습니다."),
    CREATED(HttpStatus.CREATED, "CREATED", "생성이 완료되었습니다."),
    DELETED(HttpStatus.OK, "DELETED", "삭제가 완료되었습니다."),
    UPDATED(HttpStatus.OK, "UPDATED", "수정이 완료되었습니다."),

    // Auth
    LOGIN_SUCCESS(HttpStatus.OK, "LOGIN_SUCCESS", "로그인에 성공했습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "LOGOUT_SUCCESS", "로그아웃에 성공했습니다."),
    TOKEN_REISSUED(HttpStatus.OK, "TOKEN_REISSUED", "토큰이 재발급되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
