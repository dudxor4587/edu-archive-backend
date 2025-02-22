package com.backend.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "signup_request")
public class SignupRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;

    private String password;

    private String email;

    private String name;

    @Builder
    public SignupRequest(Long userId, String userName, String password,String email, String name) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.name = name;
    }
}
