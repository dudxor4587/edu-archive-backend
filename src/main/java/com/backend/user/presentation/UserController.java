package com.backend.user.presentation;

import com.backend.user.dto.request.UserRequest;
import com.backend.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> doLogin(@RequestBody UserRequest userRequest, HttpSession session) {
        String userName = userRequest.userName();
        String password = userRequest.password();
        Long userId = userService.doLogin(userName, password);
        session.setAttribute("userId", userId);
        return ResponseEntity.ok("로그인에 성공하였습니다.");
    }

    @GetMapping("/logout")
    public ResponseEntity<String> doLogout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃에 성공하였습니다.");
    }

    @GetMapping("/session-check")
    public ResponseEntity<Map<String, Boolean>> checkSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Map<String, Boolean> response = new HashMap<>();
        response.put("isLoggedIn", userId != null);
        response.put("isAdmin", userService.isAdmin(userId));

        return ResponseEntity.ok(response);
    }
}
