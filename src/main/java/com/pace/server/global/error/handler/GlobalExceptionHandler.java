package com.pace.server.global.error.handler;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pace.server.global.common.code.ErrorCode;
import com.pace.server.global.common.response.ApiResponse;
import com.pace.server.global.error.exception.BusinessException;
import com.pace.server.global.error.exception.ExternalApiException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * BusinessException 처리
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.warn("BusinessException: {}", e.getMessage());

		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ApiResponse.error(errorCode));
	}

	/**
	 * ExternalApiException 처리
	 */
	@ExceptionHandler(ExternalApiException.class)
	public ResponseEntity<ApiResponse<Void>> handleExternalApiException(ExternalApiException e) {
		log.error("ExternalApiException - API: {}, Message: {}", e.getApiName(), e.getOriginalMessage());

		return ResponseEntity
			.status(e.getErrorCode().getHttpStatus())
			.body(ApiResponse.error(e.getErrorCode()));
	}

	/**
	 * Validation 예외 처리 (@Valid)
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
		String errorMessage = e.getBindingResult().getFieldErrors().stream()
			.map(FieldError::getDefaultMessage)
			.collect(Collectors.joining(", "));

		log.warn("Validation error: {}", errorMessage);

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error(ErrorCode.INVALID_INPUT, errorMessage));
	}

	/**
	 * BindException 처리
	 */
	@ExceptionHandler(BindException.class)
	public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
		String errorMessage = e.getBindingResult().getFieldErrors().stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.collect(Collectors.joining(", "));

		log.warn("Bind error: {}", errorMessage);

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error(ErrorCode.INVALID_INPUT, errorMessage));
	}

	/**
	 * HTTP Method 불일치 예외 처리
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodNotSupportedException(
		HttpRequestMethodNotSupportedException e) {
		log.warn("Method not allowed: {}", e.getMessage());

		return ResponseEntity
			.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED));
	}

	/**
	 * 기타 모든 예외 처리
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		log.error("Unexpected error occurred", e);

		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
	}
}
