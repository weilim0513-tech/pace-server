package com.pace.server.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common (4xx)
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "E_BAD_REQUEST", "잘못된 요청입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "E_INVALID_INPUT", "입력값이 유효하지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E_NOT_FOUND", "리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E_METHOD_NOT_ALLOWED", "허용되지 않은 메서드입니다."),

    // Auth
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "E_TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "E_TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "E_ACCESS_DENIED", "접근 권한이 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "E_RT_NOT_FOUND", "리프레시 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "E_RT_MISMATCH", "리프레시 토큰이 일치하지 않습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E_USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "E_USER_EXISTS", "이미 가입된 사용자입니다."),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "E_USER_INACTIVE", "비활성화된 계정입니다."),

    // Alarm
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "E_ALARM_NOT_FOUND", "알람을 찾을 수 없습니다."),
    ALARM_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "E_ALARM_LIMIT", "알람 설정 개수를 초과했습니다. (최대 10개)"),
    MISSION_TOO_FAST(HttpStatus.BAD_REQUEST, "E_MISSION_TOO_FAST", "미션 완료가 너무 빠릅니다. 다시 시도해주세요."),

    // Recap
    RECAP_NOT_READY(HttpStatus.NOT_FOUND, "E_RECAP_NOT_READY", "브리핑 데이터가 아직 준비되지 않았습니다."),

    // External API
    EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "E_EXTERNAL_API", "외부 서비스 연동 중 오류가 발생했습니다."),
    WEATHER_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "E_WEATHER_API", "날씨 정보를 가져올 수 없습니다."),
    NEWS_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "E_NEWS_API", "뉴스 정보를 가져올 수 없습니다."),

    // Region
    REGION_INVALID(HttpStatus.BAD_REQUEST, "E_REGION_INVALID", "지원하지 않는 지역입니다."),

    // Server (5xx)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E_INTERNAL", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
