package com.example.symptom.exceptions;

import jakarta.servlet.http.HttpServletRequest;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExists(
            UserAlreadyExistsException ex,
            HttpServletRequest request) {
        
        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "User Already Exists",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    @ExceptionHandler(RoleNotExistException.class)
    public ResponseEntity<ApiError> handleRoleNotExists(
    		RoleNotExistException ex,
    		HttpServletRequest request){
    	ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "role does not exist",
                ex.getMessage(),
                request.getRequestURI()
        );
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationError(MethodArgumentNotValidException ex,
    		HttpServletRequest request){
    	String errors = ex.getBindingResult().getFieldErrors().stream()
    			.map(err->err.getField()+": "+err.getDefaultMessage()).collect(Collectors.joining(","));
    	ApiError apiError = new ApiError(
    			HttpStatus.BAD_REQUEST.value(),
    			"Validation failed",
    			errors,
    			request.getRequestURI()
    			);
    	return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    // 👉 you can add other exception handlers here
}