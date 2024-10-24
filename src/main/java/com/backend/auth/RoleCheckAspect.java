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
import com.backend.user.exception.UnauthorizedException;
import java.lang.reflect.Method;

@Component
@Aspect
@RequiredArgsConstructor
public class RoleCheckAspect {

    private final HttpSession session;
    private final UserRepository userRepository;

    @Before("@annotation(com.backend.auth.HasRole)")
    public void checkRoleAccess(JoinPoint joinPoint) {
        User user = getUserFromSession();
        String[] requiredRoles = getRequiredRoles(joinPoint);

        if (!hasAccess(user, requiredRoles)) {
            throw new UnauthorizedException("접근 권한이 없습니다.");
        }
    }

    private Long getUserIdFromSession() {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedException("로그인 되지 않은 사용자입니다.");
        }
        return userId;
    }

    private User getUserFromSession() {
        Long userId = getUserIdFromSession();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("해당 사용자는 접근할 수 없습니다."));
    }

    private String[] getRequiredRoles(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        HasRole hasRole = method.getAnnotation(HasRole.class);
        return hasRole.value();
    }

    private boolean hasAccess(User user, String[] requiredRoles) {
        for (String role : requiredRoles) {
            if (user.getRole() == Role.valueOf(role)) {
                return true;
            }
        }
        return false;
    }
}
