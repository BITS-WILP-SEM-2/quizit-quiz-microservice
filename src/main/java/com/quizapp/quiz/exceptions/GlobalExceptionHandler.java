package com.quizapp.quiz.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException exception){
		ApiResponse error = new ApiResponse("error", exception.getMessage());
		
		return new ResponseEntity<ApiResponse>(error, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<ApiResponse> handleUnauthorizedAccessException(UnauthorizedAccessException exception){
		ApiResponse error = new ApiResponse("error", exception.getMessage());
		
		return new ResponseEntity<ApiResponse>(error, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ApiResponse> handleClientErrorException(HttpClientErrorException exception){
		ApiResponse error = new ApiResponse("error", "Invalid auth token");
		
		return new ResponseEntity<ApiResponse>(error, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<ApiResponse> handleMissingHeaderException(MissingRequestHeaderException ex){
		ApiResponse error = new ApiResponse("error", "Missing authorization header");
		
		return new ResponseEntity<ApiResponse>(error, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String,String>> handleMethodArgsNotValidException(MethodArgumentNotValidException ex){
		Map<String,String> response = new HashMap<>();
		
		ex.getBindingResult().getAllErrors().forEach((error)->{
			String fieldName = ((FieldError)error).getField();
			String message = error.getDefaultMessage();
			response.put(fieldName, message);
			
		});

		return new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {

		ApiResponse error = new ApiResponse("error", "Unknown error occured");
		return new ResponseEntity<ApiResponse>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
