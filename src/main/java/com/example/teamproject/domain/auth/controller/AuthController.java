package com.example.teamproject.domain.auth.controller;

import com.example.teamproject.domain.auth.dto.request.LoginRequest;
import com.example.teamproject.domain.auth.dto.request.RefreshRequest;
import com.example.teamproject.domain.auth.dto.request.SignupRequest;
import com.example.teamproject.domain.auth.dto.response.TokenResponse;
import com.example.teamproject.domain.auth.dto.response.UserDto;
import com.example.teamproject.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth/user")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /** 회원가입  */
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequest signupDto) {
        UserDto signed = authService.signup(signupDto);
        return ResponseEntity.ok(signed);
    }

    /**
     * 1) 로그인 → Access + Refresh Token 발급
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokens = authService.login(loginRequest);
        return ResponseEntity.ok(tokens);
    }

    /**
     * 2) Refresh Token 유효성 검증 → 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) {
        TokenResponse tokens = authService.refresh(refreshRequest);
        return ResponseEntity.ok(tokens);
    }

    /**
     * Refresh Token 유효성 확인
     */
    @GetMapping("/refresh/validate")
    public ResponseEntity<Void> validate(@RequestParam String refreshToken) {
        boolean valid = authService.validateRefreshToken(refreshToken);
        return valid
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
