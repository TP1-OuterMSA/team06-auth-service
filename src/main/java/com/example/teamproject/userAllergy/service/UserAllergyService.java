package com.example.teamproject.userAllergy.service;

import com.example.teamproject.allergy.entity.Allergy;
import com.example.teamproject.allergy.service.AllergyService;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.repository.UserRepository;
import com.example.teamproject.userAllergy.entity.UserAllergy;
import com.example.teamproject.userAllergy.repository.UserAllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAllergyService {

    private final AllergyService allergyService;
    private final UserAllergyRepository userAllergyRepository;
    private final UserRepository userRepository;

    public void saveUserAllergy(User user, Long allergyId) {
        Allergy allergy = allergyService.getAllergyById(allergyId);
        userAllergyRepository.save(UserAllergy.of(user, allergy));
    }

    public List<String> getAllergyNamesByUserId(Long userId) {
        return userAllergyRepository.findByUserId(userId).stream()
                .map(ua -> ua.getAllergy().getName())
                .toList();
    }
    private User findUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
    }
}
