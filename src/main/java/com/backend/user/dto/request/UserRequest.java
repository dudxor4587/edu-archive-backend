package com.backend.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotNull(message = "아이디는 필수 입력 값입니다.") String userName,
        @NotNull(message = "비밀번호는 필수 입력 값입니다.") String password
) {
}
