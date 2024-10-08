package com.backend.auth;

import com.backend.user.presentation.status.Role;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.domain.User;
import com.backend.common.exception.UnauthorizedException;
import java.lang.reflect.Method;

@Component
@Aspect
@RequiredArgsConstructor
public class RoleCheckAspect {

    private final HttpSession session;

    private final UserRepository userRepository;

    @Before("@annotation(com.backend.auth.HasRole)")
    public void checkRoleAccess(JoinPoint joinPoint){
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
            if(user.getRole() == Role.valueOf(role)) {
                hasAccess = true;
                break;
            }
        }

        if (!hasAccess) {
            throw new UnauthorizedException("접근 권한이 없습니다.");
        }
    }
}
