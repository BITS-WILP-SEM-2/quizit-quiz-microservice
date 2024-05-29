package com.quizapp.quiz.exceptions;

public class UnauthorizedAccessException extends RuntimeException{

	
	private static final long serialVersionUID = 1L;

	public UnauthorizedAccessException(String message) {
		super(message);
	}
	
	public UnauthorizedAccessException() {
		super("You are not authorized to do this action");
	}
	
}
