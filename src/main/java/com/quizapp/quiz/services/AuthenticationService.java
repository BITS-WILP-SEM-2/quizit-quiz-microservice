package com.quizapp.quiz.services;

import com.quizapp.quiz.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthenticationService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	private final String validationUrl = "https://users-service.cfapps.us10-001.hana.ondemand.com/users/token/validate";
	
	/**
	 * Validates the user's access token and retrieves user information.
	 *
	 * This method sends an HTTP GET request to the user service's token validation endpoint to validate the provided access token. It includes the token in the authorization header of the request. The method returns the User object containing user information extracted from the response body.
	 *
	 * @param token the access token to validate
	 * @return the User object containing user information
	 */
	public User validateRequest(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		
		ResponseEntity<User> user = restTemplate.exchange(validationUrl, HttpMethod.GET, entity, User.class);
		
		return user.getBody();
	}
	
	/**
	 * Validates whether the user is who he claims to be
	 * 
	 * @param token
	 * @param role, the role which should be allowed
	 * @return if the user is allowed
	 */
	public boolean validateRole(String token, String role) {
		User user = validateRequest(token);
		return user.getRole().equals(role);
	}
}
