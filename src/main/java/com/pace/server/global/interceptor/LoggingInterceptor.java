package com.pace.server.global.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

	private static final String START_TIME = "startTime";

	@Override
	public boolean preHandle(HttpServletRequest request,
		HttpServletResponse response,
		Object handler) {
		request.setAttribute(START_TIME, System.currentTimeMillis());
		log.debug("[REQUEST] {} {}", request.getMethod(), request.getRequestURI());
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
		HttpServletResponse response,
		Object handler, Exception ex) {
		long startTime = (Long)request.getAttribute(START_TIME);
		long duration = System.currentTimeMillis() - startTime;

		log.info("[RESPONSE] {} {} - {} ({}ms)",
			request.getMethod(),
			request.getRequestURI(),
			response.getStatus(),
			duration);
	}
}
