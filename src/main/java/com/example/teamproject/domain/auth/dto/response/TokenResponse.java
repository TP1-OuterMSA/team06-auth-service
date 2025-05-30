package com.example.teamproject.domain.auth.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;
}