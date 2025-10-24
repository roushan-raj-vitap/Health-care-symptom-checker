package com.example.symptom.dto;

import lombok.Data;
import java.util.List;

@Data
public class SymptomResponse {
    private String disclaimer;
    private List<String> probableConditions;
    private List<String> recommendedNextSteps;
    private String rawModelText; // raw text if you want to show or store
}
