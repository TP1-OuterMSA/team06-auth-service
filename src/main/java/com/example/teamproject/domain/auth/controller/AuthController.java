package com.example.teamproject.domain.auth.controller;

import com.example.teamproject.domain.auth.dto.request.LoginRequest;
import com.example.teamproject.domain.auth.dto.request.RefreshRequest;
import com.example.teamproject.domain.auth.dto.request.SignupRequest;
import com.example.teamproject.domain.auth.dto.response.AccesstokenResponse;
import com.example.teamproject.domain.auth.dto.response.JwtResponseDto;
import com.example.teamproject.domain.auth.dto.response.TokenResponse;
import com.example.teamproject.domain.auth.dto.response.UserDto;
import com.example.teamproject.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;

@RestController
@RequestMapping("api/team06-auth-service/auth/user")
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
    public ResponseEntity<AccesstokenResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        TokenResponse tokens = authService.login(loginRequest);

        // 2) HttpOnly & Secure 쿠키로 Refresh Token 전송
        Cookie refreshCookie = new Cookie("refreshToken", tokens.getRefreshToken());
        refreshCookie.setHttpOnly(true);           // JS 접근 차단
        refreshCookie.setSecure(true);             // HTTPS 환경에서만 전송
        refreshCookie.setPath("/api/auth/user/refresh"); // RT를 처리할 엔드포인트 경로
        refreshCookie.setMaxAge((int) Duration.ofDays(7).getSeconds()); // 예: 7일
        response.addCookie(refreshCookie);

        // 3) 바디엔 AccessToken만 돌려줌
        AccesstokenResponse body = new AccesstokenResponse(tokens.getAccessToken());
        return ResponseEntity.ok(body);
    }

    /**
     * 2) Refresh Token 유효성 검증 → 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<AccesstokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        // 1) 브라우저가 보낸 쿠키에서 RT 가져오기
        Cookie[] cookies = request.getCookies();
        String refreshToken = Arrays.stream(cookies != null ? cookies : new Cookie[0])
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Refresh Token이 없습니다."));

        // 2) RT 검증 → 새로운 AT/RT 발급
        TokenResponse newTokens = authService.refresh(refreshToken);

        // 3) 새 RT로 쿠키 갱신
        Cookie newRefreshCookie = new Cookie("refreshToken", newTokens.getRefreshToken());
        newRefreshCookie.setHttpOnly(true);
        newRefreshCookie.setSecure(true);
        newRefreshCookie.setPath("/api/auth/user/refresh");
        newRefreshCookie.setMaxAge((int) Duration.ofDays(7).getSeconds());
        response.addCookie(newRefreshCookie);

        // 4) 새 AT만 바디에 담아 반환
        AccesstokenResponse body = new AccesstokenResponse(newTokens.getAccessToken());
        return ResponseEntity.ok(body);
    }

    /**
     * Refresh Token 유효성 확인
     */
    @GetMapping("/refresh/validate")
    public ResponseEntity<Void> validate(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        // 쿠키에 토큰이 없으면 UNAUTHORIZED
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean valid = authService.validateRefreshToken(refreshToken);
        return valid
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
