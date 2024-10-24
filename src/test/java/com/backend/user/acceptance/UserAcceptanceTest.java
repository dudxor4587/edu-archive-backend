package com.backend.user.acceptance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.backend.user.domain.User;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.dto.request.UserRequest;
import com.backend.user.presentation.status.Role;
import com.backend.user.service.UserService;
import io.restassured.http.ContentType;
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
@DisplayName("유저 인수 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UserAcceptanceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    String sessionId;

    @BeforeEach
    void setUp() {
        userService.createUser(User.builder()
                .userName("testUser")
                .password("testPassword")
                .role(Role.MEMBER)
                .name("testName")
                .build());
        sessionId = given()
                .contentType(ContentType.JSON)
                .body(new UserRequest("testUser", "testPassword"))
                .when()
                .post("/api/users/login")
                .sessionId();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE \"user\" ALTER COLUMN \"user_id\" RESTART WITH 1");
    }

    @Test
    @DisplayName("로그인 성공")
    void 로그인_성공() {
        UserRequest loginRequest = new UserRequest("testUser", "testPassword");
        System.err.println(userRepository.findAll().get(0).getUserName());

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(200)
                .body(equalTo("로그인에 성공하였습니다."));
    }

    @Test
    @DisplayName("로그아웃 성공")
    void 로그아웃_성공() {
        given()
                .sessionId(sessionId)
                .when()
                .get("/api/users/logout")
                .then()
                .statusCode(200)
                .body(equalTo("로그아웃에 성공하였습니다."));
    }

    @Test
    @DisplayName("세션 상태 확인")
    void 세션_상태_확인() {
        given()
                .sessionId(sessionId)
                .when()
                .get("/api/users/session-check")
                .then()
                .statusCode(200)
                .body("isLoggedIn", equalTo(true))
                .body("isAdmin", equalTo(false))
                .body("isManager", equalTo(false));
    }
}
