package com.backend.visitor.scheduler;

import static com.backend.visitor.service.RedisService.REDIS_KEY_MONTHLY_VISITOR_COUNT;
import static com.backend.visitor.util.TimeUtils.getCurrentMonth;
import static com.backend.visitor.util.TimeUtils.getLastMonth;

import com.backend.visitor.service.RedisService;
import com.backend.visitor.service.VisitorUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class VisitorSyncScheduler {

    private final RedisService redisService;
    private final VisitorUpdateService visitorUpdateService;

    @Scheduled(cron = "59 23 0 * * *")
    public void syncTotalVisitorCountToDatabase() {
        log.info("syncTotalVisitorCountToDatabase 실행됨");
        Long visitorCount = redisService.getVisitorCount(getCurrentMonth());

        if (visitorCount != null) {
            visitorUpdateService.saveVisitorCount("TOTAL", visitorCount);
        }
    }

    @Scheduled(cron = "59 23 0 * * *")
    public void syncMonthlyVisitorCountToDatabase() {
        log.info("syncMonthlyVisitorCountToDatabase 실행됨");
        Long monthlyVisitorCount = redisService.getVisitorCount(getCurrentMonth());

        visitorUpdateService.saveVisitorCount(getCurrentMonth(), monthlyVisitorCount);

    }

    @Scheduled(cron = "0 10 0 1 * *")
    public void resetMonthlyVisitorCountIfMonthChanged() {
        log.info("resetMonthlyVisitorCountIfMonthChanged 실행됨");
        String lastMonth = getLastMonth();

        redisService.deleteVisitorCountMonth(lastMonth);
        redisService.setMonthlyRedisKey(REDIS_KEY_MONTHLY_VISITOR_COUNT + getCurrentMonth());
    }

}
