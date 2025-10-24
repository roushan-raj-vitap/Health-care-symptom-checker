package com.example.symptom.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.symptom.dto.JwtRequest;
import com.example.symptom.dto.JwtResponse;
import com.example.symptom.dto.UserDto;
import com.example.symptom.service.AuthService;
import com.example.symptom.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody UserDto user){
		return userService.registerUser(user);
	}
	
	@PostMapping("/login")
	public ResponseEntity<JwtResponse> loginUser(@Valid @RequestBody JwtRequest jwtRequest){
		return new ResponseEntity<>(authService.login(jwtRequest),HttpStatus.OK);
	}
}
