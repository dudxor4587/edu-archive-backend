package com.backend.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backend.user.exception.UnauthorizedException;
import com.backend.user.domain.User;
import com.backend.user.domain.repository.UserRepository;
import com.backend.user.dto.response.UserResponse;
import com.backend.user.presentation.status.Role;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DisplayName("유저 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService; // Mock 객체 선언

    @Mock
    private UserRepository userRepository; // Mock 객체 선언

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
        userService = new UserService(userRepository);
        User user = User.builder()
                .userName("testUser")
                .password("testPassword")
                .build();
        when(userRepository.findByUserName("testUser")).thenReturn(java.util.Optional.of(user));
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
        User user = mock(User.class);
        when(userRepository.findByUserName("testUser")).thenReturn(java.util.Optional.of(user));

        // when
        userService.doLogin("testUser", "testPassword");

        // then
        verify(user).updateLastLoginTime();
    }

    @Test
    void 관리자를_제외한_사용자의_정보를_가져올_수_있다(){
        // given
        User member = mock(User.class);
        when(member.getUserId()).thenReturn(1L);
        when(member.getUserName()).thenReturn("member");
        when(member.getPassword()).thenReturn("memberPassword");
        when(member.getRole()).thenReturn(Role.MEMBER);
        when(member.getName()).thenReturn("memberName");

        User manager = mock(User.class);
        when(manager.getUserId()).thenReturn(2L);
        when(manager.getUserName()).thenReturn("manager");
        when(manager.getPassword()).thenReturn("managerPassword");
        when(manager.getRole()).thenReturn(Role.MANAGER);
        when(manager.getName()).thenReturn("managerName");

        User admin = mock(User.class);
        when(admin.getUserId()).thenReturn(3L);
        when(admin.getUserName()).thenReturn("admin");
        when(admin.getPassword()).thenReturn("adminPassword");
        when(admin.getRole()).thenReturn(Role.ADMIN);
        when(admin.getName()).thenReturn("adminName");

        when(userRepository.findAllById(anyList())).thenReturn(Arrays.asList(member, manager, admin));

        // when
        List<UserResponse> userResponses = userService.getUsers(Arrays.asList(1L, 2L, 3L));

        // then
        assertEquals(2, userResponses.size());
        assertEquals("memberName", userResponses.get(0).name());
        assertEquals("managerName", userResponses.get(1).name());
    }

    @Test
    void 사용자가_없다면_가져온_정보는_비어있다() {
        // given
        when(userRepository.findAllById(anyList())).thenReturn(List.of());

        // when
        List<UserResponse> userResponses = userService.getUsers(List.of());

        // then
        assertTrue(userResponses.isEmpty());
    }

    @Test
    void 사용자의_권한을_확인할_수_있다(){
        // given
        Long userId = 1L;
        Role role = Role.MEMBER;
        User user = mock(User.class);
        when(user.getRole()).thenReturn(Role.MEMBER);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // when
        // then
        assertTrue(userService.checkPermission(userId, role));
    }

    @Test
    void 사용자를_등록할_수_있다() {
        // given
        User user = User.builder()
                .userName("testUser")
                .password("testPassword")
                .role(Role.MEMBER)
                .name("testName")
                .build();

        // when
        userService.createUser(user);

        // then
        verify(userRepository).save(user);
    }
}
