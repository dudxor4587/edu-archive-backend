package com.backend.user.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.backend.user.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DisplayName("유저 도메인 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .password("correctPassword")
                .build();

    }

    @Test
    void 사용자는_올바른_비밀번호로_로그인할_수_있다() {
        // given
        String correctPassword = "correctPassword";

        // when
        // then
        assertDoesNotThrow(() -> user.login(correctPassword));
    }

    @Test
    void 사용자는_잘못된_비밀번호로_로그인할_수_없다() {
        // given
        String wrongPassword = "wrongPassword";

        // when
        // then
        assertThrows(UnauthorizedException.class, () -> user.login(wrongPassword));
    }
}
