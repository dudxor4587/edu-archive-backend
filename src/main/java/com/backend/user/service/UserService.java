package com.backend.user.service;

import com.backend.user.domain.User;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.presentation.status.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long doLogin(String userName, String password) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.login(password);
        return user.getUserId();
    }

    public boolean isAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getRole().equals(Role.ADMIN);
    }
}
