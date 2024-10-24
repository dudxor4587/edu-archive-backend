package com.backend.user.domain;

import com.backend.user.exception.UnauthorizedException;
import com.backend.user.presentation.status.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;

    private LocalDateTime lastLoginTime;

    @Builder
    public User(String userName, String password, Role role, String name, LocalDateTime lastLoginTime) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.name = name;
        this.lastLoginTime = lastLoginTime;
    }

    public void login(String password) {
        if (this.password.equals(password)) {
            return;
        }
        throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
    }

    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
    }
}
