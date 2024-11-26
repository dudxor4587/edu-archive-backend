package com.backend.visitor.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    public static long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1); // 오늘 자정
        return ChronoUnit.SECONDS.between(now, midnight);
    }
}