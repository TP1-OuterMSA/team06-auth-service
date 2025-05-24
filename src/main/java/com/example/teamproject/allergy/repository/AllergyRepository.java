package com.example.teamproject.allergy.repository;

import com.example.teamproject.allergy.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {
}
