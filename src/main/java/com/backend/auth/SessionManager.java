package com.backend.auth;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

@Getter
@Component
public class SessionManager implements HttpSessionListener {

    private final Set<HttpSession> activeSessions = Collections.synchronizedSet(new HashSet<>());
    private final RouterFunctionMapping routerFunctionMapping;

    public SessionManager(RouterFunctionMapping routerFunctionMapping) {
        this.routerFunctionMapping = routerFunctionMapping;
    }

    public void addSession(HttpSession session) {
        activeSessions.add(session);
    }

    public void removeSession(HttpSession session) {
        activeSessions.remove(session);
    }

    public void removeSessionByUserId(Long userId) {
        activeSessions.stream()
                .filter(session -> userId.equals(session.getAttribute("userId")))
                .findFirst()
                .ifPresent(HttpSession::invalidate);
    }

    public Long getUserIdFromSession(HttpSession session) {
        return (Long) session.getAttribute("userId");
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        //세션 유효기간 5분
        event.getSession().setMaxInactiveInterval(60*5);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        removeSession(session);
    }
}
