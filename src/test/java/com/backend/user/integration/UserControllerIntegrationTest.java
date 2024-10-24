package com.backend.user.integration;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.auth.SessionManager;
import com.backend.user.domain.User;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.dto.request.UserRequest;
import com.backend.user.presentation.status.Role;
import com.backend.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("유저 컨트롤러 통합 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        sessionManager.getActiveSessions().clear();
        userService.createUser(User.builder()
                .userName("testUser")
                .password("testPassword")
                .role(Role.MEMBER)
                .name("testName")
                .build());

        userService.createUser(User.builder()
                .userName("testManager")
                .password("testPassword")
                .role(Role.MANAGER)
                .name("testName")
                .build());

        userService.createUser(User.builder()
                .userName("testAdmin")
                .password("testPassword")
                .role(Role.ADMIN)
                .name("testName")
                .build());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        // 자동생성 시퀀스 초기화
        jdbcTemplate.execute("ALTER TABLE \"user\" ALTER COLUMN \"user_id\" RESTART WITH 1");
    }

    @Test
    void 사용자는_정상적으로_로그인_할_수_있다() throws Exception {
        // given
        UserRequest userRequest = new UserRequest("testUser", "testPassword");

        // when
        ResultActions result = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .session(session));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().string("로그인에 성공하였습니다."));
    }

    @Test
    void 사용자는_정상적으로_로그아웃_할_수_있다() throws Exception {
        // given
        session.setAttribute("userId", 1L);
        sessionManager.addSession(session);

        // when
        ResultActions result = mockMvc.perform(get("/api/users/logout")
                .session(session));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    void 사용자는_권한을_확인할_수_있다() throws Exception {
        // given
        session.setAttribute("userId", 1L);

        // when
        ResultActions result = mockMvc.perform(get("/api/users/session-check")
                .session(session));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json("{\"isLoggedIn\":true,\"isAdmin\":false,\"isManager\":false}"));
    }

    @Test
    void 관리자는_활성화된_사용자를_조회할_수_있다() throws Exception {
        // given
        session.setAttribute("userId", 3L);

        // when
        ResultActions result = mockMvc.perform(get("/api/users/active-users").session(session));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    void 관리자는_사용자를_강제_로그아웃_시킬_수_있다() throws Exception {
        // given
        session.setAttribute("userId", 3L);
        // 강제 로그아웃 시킬 세션
        MockHttpSession targetSession = new MockHttpSession();
        targetSession.setAttribute("userId", 1L);
        sessionManager.addSession(targetSession);

        // when
        ResultActions result = mockMvc.perform(get("/api/users/inactive-user").param("userId", "1").session(session));

        // then
        result.andExpect(status().isOk());
    }

}
