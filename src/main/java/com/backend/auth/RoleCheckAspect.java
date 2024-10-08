package com.backend.auth;

import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.domain.User;
import com.backend.common.exception.UnauthorizedException;
import java.lang.reflect.Method;

@Component
@Aspect
public class RoleCheckAspect {

    @Autowired
    private HttpSession session;

    @Autowired
    private UserRepository userRepository;

    @Before("@annotation(com.backend.auth.HasRole)")
    public void checkRoleAccess(JoinPoint joinPoint) throws Exception {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedException("로그인되지 않은 사용자입니다.");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UnauthorizedException("유저 정보를 찾을 수 없습니다."));

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        HasRole hasRole = method.getAnnotation(HasRole.class);
        String[] requiredRoles = hasRole.value();

        boolean hasAccess = false;
        for (String role : requiredRoles) {
            if (user.getRole().equals(role)) {
                hasAccess = true;
                break;
            }
        }

        if (!hasAccess) {
            throw new UnauthorizedException("접근 권한이 없습니다.");
        }
    }
}