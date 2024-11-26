package com.backend.visitor.service;

import com.backend.visitor.domain.Visitor;
import com.backend.visitor.domain.repository.VisitorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitorUpdateService {
    private final VisitorRepository visitorRepository;
    private final RedisService redisService;

    @Transactional
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
}
