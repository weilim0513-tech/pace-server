package com.pace.server.global.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {

	private static final ZoneId KST = ZoneId.of("Asia/Seoul");

	private DateTimeUtil() {
	}

	public static LocalDateTime now() {
		return LocalDateTime.now(KST);
	}

	public static LocalDate today() {
		return LocalDate.now(KST);
	}

	public static String formatDate(LocalDate date) {
		return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
	}

	public static String formatDateTime(LocalDateTime dateTime) {
		return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * 두 시간 사이의 분 차이 계산
	 */
	public static long minutesBetween(LocalTime start, LocalTime end) {
		return Duration.between(start, end).toMinutes();
	}

	/**
	 * 오늘 특정 시간이 지났는지 확인
	 */
	public static boolean isAfterTime(LocalTime time) {
		return LocalTime.now(KST).isAfter(time);
	}
}
