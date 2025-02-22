package com.backend.user.service;

import com.backend.user.domain.SignupRequest;
import com.backend.user.domain.repository.SignupRequestRepository;
import com.backend.user.dto.response.UserListResponse;
import com.backend.user.exception.ExistUserNameException;
import com.backend.user.exception.UserNotFoundException;
import com.backend.user.domain.User;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.dto.response.UserResponse;
import com.backend.user.presentation.status.Role;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SignupRequestRepository signupRequestRepository;
    private final EmailService emailService;

    @Transactional
    public Long doLogin(String userName, String password) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        user.login(password);
        updateLastLoginTime(user);
        return user.getUserId();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .filter(user -> !user.getRole().equals(Role.ADMIN))
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    public boolean checkPermission(Long userId, Role role) {
        if(userId == null) {
            return false;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return user.getRole().equals(role);
    }

    @Transactional
    public void requestSignup(String userName, String password, String email, String name) {
        validateUsernameAvailability(userName);

        SignupRequest signupRequest = SignupRequest.builder()
                .userName(userName)
                .password(password)
                .email(email)
                .name(name)
                .build();

        signupRequestRepository.save(signupRequest);
        emailService.sendAdminNotificationEmail(name);
    }

    @Transactional
    public void approveSignupRequest(Long userId, Role role) {
        SignupRequest signupRequest = signupRequestRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        User user = User.builder()
                .userName(signupRequest.getUserName())
                .password(signupRequest.getPassword())
                .role(role)
                .name(signupRequest.getName())
                .build();

        userRepository.save(user);
        signupRequestRepository.delete(signupRequest);
        emailService.sendApprovalEmail(signupRequest.getEmail(), signupRequest.getName());
    }

    @Transactional
    public void rejectSignupRequest(Long userId) {
        SignupRequest signupRequest = signupRequestRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        signupRequestRepository.delete(signupRequest);
    }

    private void validateUsernameAvailability(String userName) {
        if (userRepository.existsByUserName(userName)) {
            throw new ExistUserNameException("이미 사용중인 아이디입니다.");
        }

        Optional<SignupRequest> signupRequest = signupRequestRepository.findByUserNameWithLock(userName);

        if (signupRequest.isPresent()) {
            throw new ExistUserNameException("이미 요청된 아이디입니다.");
        }
    }

    private void updateLastLoginTime(User user) {
        user.updateLastLoginTime();
        userRepository.save(user);
    }

    @Transactional
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<SignupRequest> getSignupRequests() {
        return signupRequestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UserListResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> !user.getRole().equals(Role.ADMIN))
                .map(user -> new UserListResponse(
                        user.getUserId(),
                        user.getName(),
                        user.getRole()
                )).toList();
    }

    @Transactional
    public void updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        user.updateRole(role);
    }
}
