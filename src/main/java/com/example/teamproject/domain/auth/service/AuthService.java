package com.example.teamproject.domain.auth.service;


import com.example.teamproject.domain.auth.dto.request.LoginRequest;
import com.example.teamproject.domain.auth.dto.request.RefreshRequest;
import com.example.teamproject.domain.auth.dto.request.SignupRequest;
import com.example.teamproject.domain.auth.dto.response.TokenResponse;
import com.example.teamproject.domain.auth.dto.response.UserDto;
import com.example.teamproject.domain.auth.security.CustomUserDetailsService;
import com.example.teamproject.domain.auth.security.JwtTokenProvider;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.repository.UserRepository;
import com.example.teamproject.userAllergy.service.UserAllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserAllergyService userAllergyService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;


    /**
     * 회원가입
     */
    public UserDto signup(SignupRequest signupDto) {
        if(userRepository.existsByUsername(signupDto.getUsername()))
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        else if(userRepository.existsByEmail(signupDto.getEmail()))
            throw new IllegalArgumentException("다른 이메일로 가입하세요.");

        User user = User.from(signupDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        List<Long> allergies = signupDto.getAllergies();
        if (allergies != null && !allergies.isEmpty()) {
            for (Long allergyId : allergies) userAllergyService.saveUserAllergy(user, allergyId);
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .build();
    }

    /**
     * 로그인 → AuthenticationManager 인증 → JWT(AT, RT) 발급, 반환
     */
    public TokenResponse login(LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        return tokenService.generateTokens(auth);
    }

    public TokenResponse refresh(String refreshToken) {
        return tokenService.refresh(refreshToken);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return tokenService.isRefreshTokenValid(refreshToken);
    }
}
