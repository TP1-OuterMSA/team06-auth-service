package com.example.teamproject.domain.auth.dto.response;

import com.example.teamproject.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String jwtToken;
    private List<String> allergies;
    private String role;

    public static UserDto from(User user, List<String> allergies) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .allergies(allergies)
                .role(user.getRole())
                .build();
    }
}
