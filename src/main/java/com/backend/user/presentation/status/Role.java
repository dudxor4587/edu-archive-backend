package com.backend.user.presentation.status;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN("관리자"),
    MANAGER("업로드가 가능한 사용자"),
    MEMBER("일반 사용자");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    @JsonCreator
    public static Role fromString(String value) {
        return Role.valueOf(value.toUpperCase());
    }
}
