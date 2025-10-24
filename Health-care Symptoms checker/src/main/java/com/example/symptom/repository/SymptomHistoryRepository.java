package com.example.symptom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.symptom.model.SymptomHistory;

import java.util.List;

public interface SymptomHistoryRepository extends JpaRepository<SymptomHistory, Long> {
    List<SymptomHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}
