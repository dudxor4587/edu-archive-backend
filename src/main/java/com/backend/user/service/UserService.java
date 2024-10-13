package com.backend.user.service;

import com.backend.common.exception.UserNotFoundException;
import com.backend.user.domain.User;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.dto.response.UserResponse;
import com.backend.user.presentation.status.Role;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long doLogin(String userName, String password) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        user.login(password);
        updateLastLoginTime(user);
        return user.getUserId();
    }

    public List<UserResponse> getUsers(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .filter(user -> !user.getRole().equals(Role.ADMIN))
                .map(UserResponse::from)
                .toList();
    }

    public boolean checkPermission(Long userId, Role role) {
        if(userId == null) {
            return false;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return user.getRole().equals(role);
    }

    private void updateLastLoginTime(User user) {
        user.updateLastLoginTime();
        userRepository.save(user);
    }
}
