package com.example.teamproject.domain.auth.service;


import com.example.teamproject.domain.auth.dto.request.LoginDto;
import com.example.teamproject.domain.auth.dto.request.SignupDto;
import com.example.teamproject.domain.auth.dto.response.UserDto;
import com.example.teamproject.domain.auth.security.JwtTokenProvider;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.repository.UserRepository;
import com.example.teamproject.userAllergy.service.UserAllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserAllergyService userAllergyService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    public UserDto signup(SignupDto signupDto) {
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
     * 로그인 → AuthenticationManager 인증 → JWT 발급 → UserDto 반환
     */
    public UserDto login(LoginDto loginDto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        String token = jwtTokenProvider.generateToken(auth);

        User user = getUserEntityByUsername(loginDto.getUsername());
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .jwtToken(token)
                .build();
    }

    // --- 내부 헬퍼 메서드 ---
    private User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
