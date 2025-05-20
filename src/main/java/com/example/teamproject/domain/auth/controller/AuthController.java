package com.example.teamproject.domain.auth.controller;

import com.example.teamproject.domain.auth.dto.request.LoginDto;
import com.example.teamproject.domain.auth.dto.request.SignupDto;
import com.example.teamproject.domain.auth.dto.response.UserDto;
import com.example.teamproject.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** 회원가입 → JWT 발급 */
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupDto signupDto) {
        UserDto signed = authService.signup(signupDto);
        return ResponseEntity.ok(signed);
    }

    /** 로그인 → JWT 발급 */
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto) {
        UserDto logged = authService.login(loginDto);
        return ResponseEntity.ok(logged);
    }
}
