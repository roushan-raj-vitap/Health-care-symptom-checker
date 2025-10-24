package com.example.symptom.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.symptom.dto.JwtResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
@Component
public class JwtAuthenticationHelper {
	private String secret = "thisisacodingninjasdemonstrationforsecretkeyinspringsecurityjsonwebtokenauthentication";
	private static final long JWT_TOKEN_VALIDITY = 60*60;

	public JwtResponse generateToken(UserDetails userDetails) {
	    // 1. Prepare claims (payload)
	    Map<String, Object> claims = new HashMap<>();
	    // Optional: you can add roles or other info
	    claims.put("roles", userDetails.getAuthorities());

	    // 2. Generate JWT token
	    String token = Jwts.builder()
	            .setClaims(claims)
	            .setSubject(userDetails.getUsername()) // username or userId
	            .setIssuedAt(new Date(System.currentTimeMillis()))
	            .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
	            .signWith(
	                new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName()),
	                SignatureAlgorithm.HS512
	            )
	            .compact();

	    // 3. Build response
	    JwtResponse response = new JwtResponse();
	    response.setToken(token);
	    response.setUsername(userDetails.getUsername());
	    response.setRoles(userDetails.getAuthorities().stream()
	                            .map(auth -> auth.getAuthority())
	                            .collect(Collectors.toList()));
	    response.setExpiresIn(JWT_TOKEN_VALIDITY); // in seconds, same as your constant // in total 1hr validity

	    return response;
	}

	public String getUsernameFromToken(String token) {
		// TODO Auto-generated method stub
		Claims claim = getClaimFromToken(token);
		return claim.getSubject();
	}

	private Claims getClaimFromToken(String token) {//io.jsonwebtoken.security.SignatureException if token is tempered with
		// TODO Auto-generated method stub
		return Jwts.parserBuilder().setSigningKey(secret.getBytes())
				.build().parseClaimsJws(token).getBody();
	}

	public boolean isTokenExpired(String token) {
		// TODO Auto-generated method stub
		Claims claim = getClaimFromToken(token);
		Date expDate = claim.getExpiration();
		return expDate.before(new Date());
	}
	
}
