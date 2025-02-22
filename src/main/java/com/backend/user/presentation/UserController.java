package com.backend.user.presentation;

import com.backend.auth.HasRole;
import com.backend.auth.SessionManager;
import com.backend.user.domain.SignupRequest;
import com.backend.user.dto.request.UserRequest;
import com.backend.user.dto.request.UserSignupRequest;
import com.backend.user.dto.response.UserListResponse;
import com.backend.user.dto.response.UserResponse;
import com.backend.user.presentation.status.Role;
import com.backend.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final SessionManager sessionManager;

    @PostMapping("/login")
    public ResponseEntity<String> doLogin(@RequestBody @Valid UserRequest userRequest, HttpSession session) {
        String userName = userRequest.userName();
        String password = userRequest.password();
        Long userId = userService.doLogin(userName, password);
        session.setAttribute("userId", userId);
        sessionManager.addSession(session);
        return ResponseEntity.ok("로그인에 성공하였습니다.");
    }

    @GetMapping("/logout")
    public ResponseEntity<String> doLogout(HttpSession session) {
        sessionManager.removeSession(session);
        session.invalidate();
        return ResponseEntity.ok("로그아웃에 성공하였습니다.");
    }

    @GetMapping("/session-check")
    public ResponseEntity<Map<String, Boolean>> checkSession(HttpSession session) {
        Long userId = sessionManager.getUserIdFromSession(session);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isLoggedIn", userId != null);
        response.put("isAdmin", userService.checkPermission(userId, Role.ADMIN));
        response.put("isManager", userService.checkPermission(userId, Role.MANAGER));

        return ResponseEntity.ok(response);
    }

    @HasRole({"ADMIN"})
    @GetMapping("/active-users")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        List<Long> activeUsers = sessionManager.getActiveSessions().stream()
                .map(sessionManager::getUserIdFromSession)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userService.getUsers(activeUsers));
    }

    @HasRole({"ADMIN"})
    @GetMapping("/inactive-user")
    public ResponseEntity<String> getInactiveUsers(@RequestParam("userId") Long userId) {
        sessionManager.removeSessionByUserId(userId);
        return ResponseEntity.ok("사용자를 강제 로그아웃 시켰습니다.");
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> doSignUp(@RequestBody @Valid UserSignupRequest userSignupRequest) {
        String userName = userSignupRequest.userName();
        String password = userSignupRequest.password();
        String email = userSignupRequest.email();
        String name = userSignupRequest.name();

        userService.requestSignup(userName, password, email, name);
        return ResponseEntity.ok("회원가입 요청에 성공하였습니다.");
    }

    @HasRole({"ADMIN"})
    @GetMapping("/approve")
    public ResponseEntity<String> approveUser(@RequestParam("userId") Long userId, @RequestParam("role") Role role) {
        userService.approveSignupRequest(userId, role);
        return ResponseEntity.ok("회원가입 요청이 수락되었습니다.");
    }

    @HasRole({"ADMIN"})
    @GetMapping("/reject")
    public ResponseEntity<String> rejectUser(@RequestParam("userId") Long userId) {
        userService.rejectSignupRequest(userId);
        return ResponseEntity.ok("회원가입 요청이 거절되었습니다.");
    }

    @HasRole({"ADMIN"})
    @GetMapping("/signup-requests")
    public ResponseEntity<List<SignupRequest>> signupRequestLists() {
        List<SignupRequest> list = userService.getSignupRequests();
        return ResponseEntity.ok(list);
    }

    @HasRole({"ADMIN"})
    @PatchMapping("/{userId}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable("userId") Long userId, @RequestBody Role role) {
        userService.updateUserRole(userId, role);
        return ResponseEntity.ok("권한 업데이트에 성공하였습니다.");
    }

    @HasRole({"ADMIN"})
    @GetMapping
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
