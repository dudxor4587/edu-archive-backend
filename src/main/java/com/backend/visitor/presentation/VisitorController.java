package com.backend.visitor.presentation;

import static com.backend.visitor.util.TimeUtils.getCurrentMonth;

import com.backend.auth.HasRole;
import com.backend.visitor.dto.response.MonthlyVisitorResponse;
import com.backend.visitor.service.VisitorUpdateService;
import com.backend.visitor.util.CookieUtils;
import com.backend.visitor.util.TimeUtils;
import com.backend.visitor.dto.response.TotalVisitorResponse;
import com.backend.visitor.service.RedisService;
import com.backend.visitor.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/visitors")
public class VisitorController {
    private final VisitorService visitorService;
    private final RedisService redisService;
    private final VisitorUpdateService visitorUpdateService;

    @GetMapping
    public ResponseEntity<Void> visit(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String visitorId = determineVisitorId(request, response, session);

        boolean isNewVisitor = visitorService.recordVisit(visitorId);

        if (isNewVisitor) {
            log.info("New visitor recorded: {}", visitorId);
        }

        return ResponseEntity.ok().build();
    }

    @HasRole("ADMIN")
    @GetMapping("/count")
    public ResponseEntity<TotalVisitorResponse> getVisitorCount() {
        Long visitorCount = redisService.getVisitorCount("TOTAL");
        visitorUpdateService.saveVisitorCount("TOTAL", visitorCount);
        return ResponseEntity.ok(new TotalVisitorResponse(visitorCount));
    }

    @HasRole("ADMIN")
    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyVisitorResponse>> getMonthlyVisitorCount() {
        visitorUpdateService.saveVisitorCount(getCurrentMonth(), redisService.getVisitorCount(getCurrentMonth()));
        return ResponseEntity.ok(visitorService.getMonthlyVisitorCount());
    }

    private String determineVisitorId(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            return "USER:" + userId;
        }

        return handleAnonymousUser(request, response);
    }

    private String handleAnonymousUser(HttpServletRequest request, HttpServletResponse response) {
        String visitorId = CookieUtils.getCookieValue(request, "visitorId");

        if (visitorId == null) {
            visitorId = UUID.randomUUID().toString();

            long secondsUntilMidnight = TimeUtils.getSecondsUntilMidnight();

            CookieUtils.createCookie(response, "visitorId", visitorId, (int) secondsUntilMidnight);
        }

        return visitorId;
    }

}
