package com.backend.user.domain.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.backend.user.domain.User;
import com.backend.user.presentation.status.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DisplayName("유저 레포지토리 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository; // Mock 객체 선언

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
        // 테스트할 유저 데이터 초기화
        user = User.builder()
                .userName("testUser")
                .password("correctPassword")
                .build();
    }

    @Test
    void 존재하는_사용자를_찾을_수_있다() {
        // given
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));

        // when
        Optional<User> foundUser = userRepository.findByUserName("testUser");

        // then
        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUserName());
    }

    @Test
    void 존재하지_않는_사용자는_비어있다() {
        // given
        when(userRepository.findByUserName("nonExistentUser")).thenReturn(Optional.empty());

        // when
        Optional<User> foundUser = userRepository.findByUserName("nonExistentUser");

        // then
        assertFalse(foundUser.isPresent());
    }
}
