package com.example.symptom.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class SymptomRequest {
    @NotBlank
    private String symptoms;
    private Integer age;         // optional
    private String sex;          // optional
    private String existingConditions; // optional free text
}
