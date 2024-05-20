package com.quizapp.quiz.controllers;

import com.quizapp.quiz.entities.Submissions;
import com.quizapp.quiz.entities.User;
import com.quizapp.quiz.exceptions.UnauthorizedAccessException;
import com.quizapp.quiz.services.AuthenticationService;
import com.quizapp.quiz.services.SubmissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
public class SubmissionsController {
	
	@Autowired
	@Lazy
	public SubmissionsService submissionsServiceObj;
	
	@Autowired
	public AuthenticationService authenticationService;
	
	/**
	 * Retrieves submissions for a specific quiz.
	 *
	 * This method allows an authorized user to retrieve submissions for a specific quiz identified by the provided quizId. The user's access token is used to authenticate the request. If the user is unauthorized, an UnauthorizedAccessException is thrown. The method returns a list of Submissions objects containing the submissions made for the specified quiz.
	 *
	 * @param quizId the ID of the quiz for which submissions should be retrieved
	 * @param authorization the authorization header containing the user's access token
	 * @return a list of Submissions objects representing the submissions made for the specified quiz
	 * @throws UnauthorizedAccessException if the user making the request is unauthorized
	 */
	@GetMapping("/quiz/submissions")
	public List<Submissions> getSubmissions(@RequestParam Long quizId, @RequestHeader String authorization){
		authenticationService.validateRequest(authorization);
		return this.submissionsServiceObj.getSubmissions(quizId, authorization);
	}
	
	/**
	 * Retrieves submissions made by a specific user.
	 *
	 * This method allows an authorized user to retrieve their own submissions. The user's access token is used to authenticate the request. If the user is unauthorized or not a regular user, an UnauthorizedAccessException is thrown. The method returns a list of HashMap objects containing information about the user's submissions, such as the quiz ID and score.
	 *
	 * @param authorization the authorization header containing the user's access token
	 * @return a list of HashMap objects representing the user's submissions, including quiz ID and score
	 * @throws UnauthorizedAccessException if the user making the request is unauthorized or not a regular user
	 */
	@GetMapping("/quiz/user/submissions")
	public List<HashMap<String,String>> getSubmissionsByUserId(@RequestHeader String authorization){
		User user = authenticationService.validateRequest(authorization);
		if(authenticationService.validateRole(authorization, "USER")) {
			return this.submissionsServiceObj.getSubmissionsByUserId(user.getUserId());
		}
		else {
			throw new UnauthorizedAccessException();
		}
		
	}

}
