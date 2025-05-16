package com.example.teamproject.userAllergy.entity;

import com.example.teamproject.allergy.entity.Allergy;
import com.example.teamproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserAllergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergy_id")
    private Allergy allergy;

    public static UserAllergy of(User user, Allergy allergy) {
        UserAllergy userAllergy = new UserAllergy();
        userAllergy.user = user;
        userAllergy.allergy = allergy;
        return userAllergy;
    }
}
