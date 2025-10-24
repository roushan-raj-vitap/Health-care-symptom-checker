package com.example.symptom.dto;

import java.util.List;
import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
	private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;
    private long expiresIn; // in milliseconds
}
