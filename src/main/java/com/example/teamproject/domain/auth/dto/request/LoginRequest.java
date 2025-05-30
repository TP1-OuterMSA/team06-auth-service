package com.example.teamproject.domain.auth.dto.request;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String username;
    private String password;
}
