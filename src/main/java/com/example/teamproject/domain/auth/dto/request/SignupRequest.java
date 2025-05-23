package com.example.teamproject.domain.auth.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private String nickname;
    private List<Long> allergies;
}
