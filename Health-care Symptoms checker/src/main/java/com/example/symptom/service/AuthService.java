package com.example.symptom.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.symptom.dto.JwtRequest;
import com.example.symptom.dto.JwtResponse;
import com.example.symptom.jwt.JwtAuthenticationHelper;
@Service
public class AuthService {
	
	@Autowired
	AuthenticationManager manager;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	JwtAuthenticationHelper jwtAuthenticationFilter;

	public JwtResponse login(JwtRequest jwtRequest) {
		// TODO Auto-generated method stub
		this.doAuthenticate(jwtRequest.getEmail(),jwtRequest.getPassword());
		UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getEmail());
		JwtResponse jwtResponse =jwtAuthenticationFilter.generateToken(userDetails);
		return jwtResponse;
	}
	public void doAuthenticate(String email,String password) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,password);
		try {
			manager.authenticate(authenticationToken);
		}
		catch(BadCredentialsException e) {
			throw new BadCredentialsException("credential is not valid!");
		}
	}

}
