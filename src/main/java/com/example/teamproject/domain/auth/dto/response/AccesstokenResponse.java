package com.example.teamproject.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class AccesstokenResponse {
    private final String accessToken;
}
