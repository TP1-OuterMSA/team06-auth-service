package com.example.teamproject.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponseDto {
    private String token;
    private String tokenType = "Bearer";
}
