package com.backend.visitor.presentation;

import com.backend.visitor.util.CookieUtils;
import com.backend.visitor.util.TimeUtils;
import com.backend.visitor.dto.response.VisitorResponse;
import com.backend.visitor.service.RedisService;
import com.backend.visitor.service.VisitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

    @GetMapping
    public ResponseEntity<Void> visit(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String visitorId = determineVisitorId(request, response, session);

        boolean isNewVisitor = visitorService.recordVisit(visitorId);

        if (isNewVisitor) {
            log.info("New visitor recorded: {}", visitorId);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<VisitorResponse> getVisitorCount() {
        Long visitorCount = redisService.getVisitorCount("TOTAL");
        return ResponseEntity.ok(new VisitorResponse(visitorCount));
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
