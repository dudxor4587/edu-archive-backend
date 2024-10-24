package com.backend.user.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.backend.user.domain.User;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.dto.response.UserResponse;
import com.backend.user.exception.UnauthorizedException;
import com.backend.user.presentation.status.Role;
import com.backend.user.service.UserService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("유저 서비스 통합 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
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
                .name("managerName")
                .build());

        userService.createUser(User.builder()
                .userName("testAdmin")
                .password("testPassword")
                .role(Role.ADMIN)
                .name("adminName")
                .build());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        // 자동생성 시퀀스 초기화
        jdbcTemplate.execute("ALTER TABLE \"user\" ALTER COLUMN \"user_id\" RESTART WITH 1");
    }

    @Test
    void 사용자는_올바른_정보로_로그인할_수_있다(){
        //given
        String userName = "testUser";
        String password = "testPassword";

        //when
        //then
        assertDoesNotThrow(() -> userService.doLogin(userName, password));
    }

    @Test
    void 사용자는_올바르지_않은_정보로_로그인할_수_없다(){
        //given
        String userName = "testUser";
        String password = "wrongPassword";

        //when
        //then
        assertThrows(UnauthorizedException.class, () -> userService.doLogin(userName, password));
    }

    @Test
    void 로그인_시에_사용자의_마지막_로그인_날짜가_업데이트될_수_있다() {
        // given
        // when
        userService.doLogin("testUser", "testPassword");

        // 업데이트된 마지막 로그인 시간
        LocalDateTime lastLoginTime = userRepository.findByUserName("testUser").get().getLastLoginTime();
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // 시간 차이 계산
        Duration duration = Duration.between(lastLoginTime, now);

        // then
        assertTrue(duration.abs().toSeconds() < 10, "로그인 시간이 업데이트 되지 않았습니다.");
    }

    @Test
    void 관리자를_제외한_사용자의_정보를_가져올_수_있다(){
        // given
        List<Long> userIds = userRepository.findAll().stream()
                .map(User::getUserId)
                .toList();

        // when
        List<UserResponse> userResponses = userService.getUsers(userIds);

        // then
        assertEquals(2, userResponses.size());
        assertEquals("testName", userResponses.get(0).name());
        assertEquals("managerName", userResponses.get(1).name());
    }

    @Test
    void 사용자의_권한을_확인할_수_있다(){
        // given
        Long userId = 1L;
        Role role = Role.MEMBER;
        // when
        // then
        assertTrue(userService.checkPermission(userId, role));
    }

    @Test
    void 사용자를_등록할_수_있다() {
        // given
        User user = User.builder()
                .userName("addUser")
                .password("addPassword")
                .role(Role.MEMBER)
                .name("addName")
                .build();

        // when
        userService.createUser(user);

        // then
        assertEquals(user.getName(), userRepository.findByUserName("addUser").get().getName());
    }
}
