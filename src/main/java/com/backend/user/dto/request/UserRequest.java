package com.backend.user.dto.request;

public record UserRequest(
        String userName,
        String password
) {
}
