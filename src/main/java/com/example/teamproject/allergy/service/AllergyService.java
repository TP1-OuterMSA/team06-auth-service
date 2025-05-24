package com.example.teamproject.allergy.service;

import com.example.teamproject.allergy.entity.Allergy;
import com.example.teamproject.allergy.repository.AllergyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllergyService {

    private final AllergyRepository allergyRepository;

    public Allergy addAllergy(String name) {
        return allergyRepository.save(Allergy.of(name));
    }

    public Allergy getAllergyById(Long allergyId) {
        return allergyRepository.findById(allergyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알레르기입니다."));
    }

    public List<Allergy> getAllAllergies() {
        return allergyRepository.findAll();
    }
}
