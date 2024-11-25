package com.backend.visitor.scheduler;

import com.backend.visitor.service.RedisService;
import com.backend.visitor.service.VisitorService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class VisitorSyncScheduler {

    private final RedisService redisService;
    private final VisitorService visitorService;

    @Scheduled(cron = "0 0 0 * * *")
    public void syncTotalVisitorCountToDatabase() {
        log.info("syncTotalVisitorCountToDatabase 실행됨");
        Long visitorCount = redisService.getVisitorCount(getCurrentMonth());

        if (visitorCount != null) {
            visitorService.saveVisitorCount("TOTAL", visitorCount);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void syncMonthlyVisitorCountToDatabase() {
        log.info("syncMonthlyVisitorCountToDatabase 실행됨");
        Long monthlyVisitorCount = redisService.getVisitorCount(getCurrentMonth());

        visitorService.saveVisitorCount(getCurrentMonth(), monthlyVisitorCount);

    }

    @Scheduled(cron = "0 10 0 1 * *")
    public void resetMonthlyVisitorCountIfMonthChanged() {
        log.info("resetMonthlyVisitorCountIfMonthChanged 실행됨");
        String lastMonth = getLastMonth();

        redisService.deleteVisitorCountMonth(lastMonth);
    }

    private String getCurrentMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    private String getLastMonth() {
        return LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
}
