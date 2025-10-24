package com.example.symptom.dto;
import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class JwtRequest {
	
	String email;
	String password;

}
