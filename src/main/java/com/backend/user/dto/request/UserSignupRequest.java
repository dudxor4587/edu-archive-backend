package com.backend.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserSignupRequest(
        @NotNull(message = "아이디는 필수 입력 값입니다.") String userName,
        @NotNull(message = "비밀번호는 필수 입력 값입니다.") String password,
        @NotNull(message = "이메일은 필수 입력 값입니다.") String email,
        @NotNull(message = "이름은 필수 입력 값입니다.") String name
) {
}
