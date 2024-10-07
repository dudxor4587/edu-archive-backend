package com.backend.user.domain;

import com.backend.user.presentation.status.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void login(String companyPassword) {
        if (this.password.equals(companyPassword)) {
            return;
        }
        throw new IllegalArgumentException("Password does not match");
    }
}
