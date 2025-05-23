package com.example.teamproject.domain.auth.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;

    @Override
    public String toString() {
        return "TokenResponse{" +
                "accessToken='" + accessToken + '\\' +
        ", refreshToken='" + refreshToken + '\\' +
        '}';
    }
}