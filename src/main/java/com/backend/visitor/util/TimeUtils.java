package com.backend.visitor.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    public static long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1); // 오늘 자정
        return ChronoUnit.SECONDS.between(now, midnight);
    }

    public static String getCurrentMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    public static String getLastMonth() {
        return LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
}
