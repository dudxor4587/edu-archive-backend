package com.backend.user.dto.response;

import com.backend.user.presentation.status.Role;

public record UserListResponse(
        Long userId,
        String name,
        Role role
) {
}
