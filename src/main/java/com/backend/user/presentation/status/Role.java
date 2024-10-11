package com.backend.user.presentation.status;

public enum Role {
    ADMIN("관리자"),
    MANAGER("업로드가 가능한 사용자"),
    MEMBER("일반 사용자");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
