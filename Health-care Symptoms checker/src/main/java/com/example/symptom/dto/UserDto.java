package com.example.symptom.dto;

import lombok.Builder;
import java.util.List;

import com.example.symptom.model.Role;

import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {
	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	private String email;
	
	
	private String username;
	@NotBlank(message = "Password is required")
	private String password;
	private String role;
}
