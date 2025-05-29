package com.example.teamproject.domain.user.entity;

import com.example.teamproject.domain.auth.dto.request.SignupRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String username;
    @Setter
    private String email;
    @Setter
    private String password;
    @Setter
    private String nickname;
    @Setter
    private String role;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    @Setter
    private byte[] profileImage;

    @Column(name = "profile_image_type")
    @Setter
    private String profileImageType;

    public static User from(SignupRequest dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .role("USER")
                .build();
    }

}
