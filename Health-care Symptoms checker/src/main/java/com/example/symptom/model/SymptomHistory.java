package com.example.symptom.model;
import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "symptom_history")
@Getter @Setter @NoArgsConstructor
public class SymptomHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // link to your user table id (nullable if anonymous)
    @Column(length = 4000)
    private String requestText;
    @Column(length = 8000)
    private String responseText;
    private Instant createdAt = Instant.now();
}
