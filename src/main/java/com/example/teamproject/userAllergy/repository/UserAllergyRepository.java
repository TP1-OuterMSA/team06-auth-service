package com.example.teamproject.userAllergy.repository;

import com.example.teamproject.userAllergy.entity.UserAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAllergyRepository extends JpaRepository<UserAllergy, Long> {
    // Custom query methods can be defined here if needed

    List<UserAllergy> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    void deleteAllByUserIdAndAllergyIdIn(Long userId, List<Long> allergyIds);

}
