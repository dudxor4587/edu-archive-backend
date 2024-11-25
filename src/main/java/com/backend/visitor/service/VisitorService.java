package com.backend.visitor.service;

import static com.backend.visitor.service.RedisService.REDIS_KEY_MONTHLY_VISITOR_COUNT;
import static com.backend.visitor.service.RedisService.REDIS_KEY_TOTAL_VISITOR_COUNT;
import static com.backend.visitor.service.RedisService.REDIS_KEY_VISITOR;

import com.backend.visitor.util.TimeUtils;
import com.backend.visitor.domain.Visitor;
import com.backend.visitor.domain.repository.VisitorRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitorService {
    private final RedisService redisService;
    private final VisitorRepository visitorRepository;

    public boolean recordVisit(String visitorId) {
        String redisKey = REDIS_KEY_VISITOR + visitorId;

        if (redisService.isVisitorExist(redisKey)) {
            return false;
        }

        long secondsUntilMidnight = TimeUtils.getSecondsUntilMidnight();

        redisService.setVisitorKey(redisKey, "true", secondsUntilMidnight);
        redisService.incrementVisitorCount(REDIS_KEY_TOTAL_VISITOR_COUNT);
        redisService.incrementVisitorCount(REDIS_KEY_MONTHLY_VISITOR_COUNT + getCurrentMonth());

        return true;
    }

    public void saveVisitorCount(String range, Long visitorCount) {
        Visitor visitor = visitorRepository.findByTargetRange(range);
        if (visitor == null) {
            visitor = Visitor.builder()
                    .targetRange(range)
                    .visitorCount(visitorCount)
                    .build();
        } else if (visitor.getVisitorCount() < visitorCount) {
            visitor.updateVisitorCount(visitorCount);
        }
        visitorRepository.save(visitor);
        redisService.updateVisitorCount(range, Math.max(visitor.getVisitorCount(), visitorCount));
    }

    private String getCurrentMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
}
