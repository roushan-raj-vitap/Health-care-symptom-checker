package com.example.symptom.exceptions;

public class RoleNotExistException extends RuntimeException{
	public RoleNotExistException(String message) {
		super(message);
	}
}
