package com.example.teamproject.domain.auth.service;

import com.example.teamproject.domain.auth.dto.request.RefreshRequest;
import com.example.teamproject.domain.auth.dto.response.TokenResponse;
import com.example.teamproject.domain.auth.security.CustomUserDetailsService;
import com.example.teamproject.domain.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redis;
    private final CustomUserDetailsService customUserDetailsService;

    private String userSetKey(String username) {
        return "user:" + username + ":rtSet";
    }

    /**
     * Access, Refresh Token 발급
     */
    public TokenResponse generateTokens(Authentication auth) {
        String access  = jwtTokenProvider.createAccessToken(auth);
        String refresh = jwtTokenProvider.createRefreshToken(auth);
        String jti      = jwtTokenProvider.getJti(refresh);
        String user     = auth.getName();

        // 1) JTI → username 매핑
        redis.opsForValue().set(jti, user, jwtTokenProvider.getRefreshExpire());
        // 2) username → JTI 집합에도 추가 (재사용 감지 & 전체 무효화용)
        redis.opsForSet().add(userSetKey(user), jti);
        redis.expire(userSetKey(user), jwtTokenProvider.getRefreshExpire());
        return new TokenResponse(access, refresh);
    }

    public TokenResponse refresh(String oldRefresh) {
        // 1) 서명·만료 검증
        if (!jwtTokenProvider.validateToken(oldRefresh)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }
        // 2) 클레임에서 username, oldJti 추출
        String username = jwtTokenProvider.getUsername(oldRefresh);
        String oldJti   = jwtTokenProvider.getJti(oldRefresh);

        // 3) 재사용 감지: Redis에 oldJti가 남아 있어야 '첫 사용'
        Boolean exists = redis.hasKey(oldJti);
        if (exists == null || !exists) {
            // A 토큰이 이미 사용되었거나 삭제된 경우 → 재사용 감지
            // 전체 RT 무효화: userSetKey(username)에 있는 모든 JTI, 그리고 value 매핑 삭제
            Set<String> allJtis =
                    redis.opsForSet().members(userSetKey(username));
            if (allJtis != null) {
                allJtis.forEach(redis::delete);
            }
            redis.delete(userSetKey(username));

            throw new RuntimeException(
                    "Refresh token reuse detected – all sessions revoked. Please login again.");
        }

        // 4) 정상 회전: 이전 RT 삭제
        redis.delete(oldJti);
        redis.opsForSet().remove(userSetKey(username), oldJti);

        // 5) 새 토큰 발급 & 저장 (generateTokens 내부 로직 재사용)
        UserDetails user = customUserDetailsService.loadUserByUsername(username);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        return generateTokens(auth);
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return false;
        }
        String jti = jwtTokenProvider.getJti(refreshToken);
        Boolean exists = redis.hasKey(jti);
        return exists != null && exists;
    }
}
