package com.backend.user.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.backend.auth.RoleCheckAspect;
import com.backend.auth.SessionManager;
import com.backend.user.exception.UnauthorizedException;
import com.backend.user.exception.UserNotFoundException;
import com.backend.user.domain.User;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.dto.request.UserRequest;
import com.backend.user.presentation.status.Role;
import com.backend.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@DisplayName("유저 컨트롤러 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private SessionManager sessionManager;

    @MockBean
    private RoleCheckAspect roleCheckAspect;

    @Mock
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userName("testUser")
                .password("correctPassword")
                .role(Role.MEMBER)
                .name("testName")
                .build();
    }

    private User createMockUser(Long id, String username, String password, Role role, String name) {
        User user = mock(User.class);
        when(user.getUserId()).thenReturn(id);
        when(user.getUserName()).thenReturn(username);
        when(user.getPassword()).thenReturn(password);
        when(user.getRole()).thenReturn(role);
        when(user.getName()).thenReturn(name);
        return user;
    }

    @Test
    void 사용자는_로그인을할_수_있다 () throws Exception {
        // given
        UserRequest userRequest = new UserRequest("testUser", "correctPassword");
        when(userService.doLogin("testUser", "correctPassword")).thenReturn(1L);

        // when, then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("로그인에 성공하였습니다."));
    }

    @Test
    void 사용자는_잘못된_아이디로_로그인할_수_없다() throws Exception {
        // given
        UserRequest userRequest = new UserRequest("wrongUser", "correctPassword");
        when(userService.doLogin("wrongUser", "correctPassword")).thenThrow(new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // when, then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void 사용자는_잘못된_비밀번호로_로그인할_수_없다() throws Exception {
        // given
        UserRequest userRequest = new UserRequest("testUser", "wrongPassword");
        when(userService.doLogin("testUser", "wrongPassword")).thenThrow(new UnauthorizedException("비밀번호가 일치하지 않습니다."));

        // when, then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 사용자는_로그아웃을_할_수_있다() throws Exception {
        // when, then
        mockMvc.perform(get("/api/users/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("로그아웃에 성공하였습니다."));
    }

    @Test
    void 사용자는_세션체크를_진행할_수_있다() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();  // Create a mock session
        session.setAttribute("userId", 1L);

        when(sessionManager.getUserIdFromSession(any(HttpSession.class))).thenReturn(1L);
        when(userService.checkPermission(1L, Role.ADMIN)).thenReturn(false);
        when(userService.checkPermission(1L, Role.MANAGER)).thenReturn(false);

        // when, then
        mockMvc.perform(get("/api/users/session-check").session(session))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"isLoggedIn\":true,\"isAdmin\":false,\"isManager\":false}"));
    }

    @Test
    void 현재_접속중인_사용자를_조회할_수_있다() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);

        when(sessionManager.getActiveSessions()).thenReturn(Set.of(session));
        when(sessionManager.getUserIdFromSession(any(HttpSession.class))).thenReturn(1L);

        // when, then
        mockMvc.perform(get("/api/users/active-users").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void 현재_접속중인_사용자를_강제_로그아웃_시킬수_있다() throws Exception {
        // given
        doNothing().when(sessionManager).removeSessionByUserId(any(Long.class));

        // when, then
        mockMvc.perform(get("/api/users/inactive-user").param("userId", "1")) // session 추가
                .andExpect(status().isOk());
    }

}
