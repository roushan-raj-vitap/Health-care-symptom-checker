package com.example.symptom.service;

import org.springframework.stereotype.Service;

import com.example.symptom.dto.JwtRequest;
import com.example.symptom.dto.JwtResponse;
import com.example.symptom.dto.UserDto;
import com.example.symptom.exceptions.RoleNotExistException;
import com.example.symptom.exceptions.UserAlreadyExistsException;
import com.example.symptom.model.Role;
import com.example.symptom.model.User;
import com.example.symptom.repository.RoleRepository;
import com.example.symptom.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AuthService authService;
	
	@Autowired
	BCryptPasswordEncoder encoder;
	
	@Autowired
	RoleRepository roleRepository;

	public ResponseEntity<JwtResponse> registerUser(UserDto userdto) {
	    // Encode the password
	    String password = encoder.encode(userdto.getPassword());

	    // Check if user already exists
	    boolean isUserExist = userRepository.existsByEmail(userdto.getEmail());
	    if(isUserExist) {
	        throw new UserAlreadyExistsException("Email "+userdto.getEmail()+" is already taken");
	    }
	    if(!roleRepository.existsByName(userdto.getRole())) {
	    	throw new RoleNotExistException("Role does not exist in database");
	    }
	    Role role = new Role();
	    role.setName(userdto.getRole());
	    List<Role> roles = new ArrayList<>();
	    roles.add(role);
	    // Build user entity
	    User user = User.builder()
	            .username(userdto.getUsername())
	            .email(userdto.getEmail())
	            .password(password)
	            .role(roles)  // now all roles are managed entities
	            .build();

	    userRepository.save(user);

	    // Create JWT token
	    JwtRequest jwtRequest = new JwtRequest(userdto.getEmail(), userdto.getPassword());
	    return new ResponseEntity<>(authService.login(jwtRequest), HttpStatus.OK);
	}

}
