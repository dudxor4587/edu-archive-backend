package com.backend.user.dto.response;

import com.backend.user.domain.User;

public record UserResponse(
    Long userId,
    String name
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getUserId(), user.getName());
    }
}
