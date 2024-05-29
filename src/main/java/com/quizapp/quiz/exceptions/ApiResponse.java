package com.quizapp.quiz.exceptions;

public class ApiResponse {

	public String responseType;
	public String message;
	
	public ApiResponse(String responseType, String message) {
		super();
		this.responseType = responseType;
		this.message = message;
	}
	public ApiResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}