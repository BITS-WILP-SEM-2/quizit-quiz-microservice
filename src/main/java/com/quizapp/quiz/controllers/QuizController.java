package com.quizapp.quiz.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.quizapp.quiz.entities.Questions;
import com.quizapp.quiz.entities.Quiz;
import com.quizapp.quiz.entities.Submissions;
import com.quizapp.quiz.entities.User;
import com.quizapp.quiz.exceptions.UnauthorizedAccessException;
import com.quizapp.quiz.services.AuthenticationService;
import com.quizapp.quiz.services.QuizService;
import com.quizapp.quiz.services.SubmissionsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quiz")
@CrossOrigin
public class QuizController {

	@Autowired
	@Lazy
	private QuizService quizObj;
	
	@Autowired
	@Lazy
	private SubmissionsService submissionsServiceObj;
	
	@Autowired
	private AuthenticationService authenticationService;

	
	/**
	 * Retrieves data of all the quizzes in the database.
	 *
	 * This method fetches the details of all the quizzes in the database. Only users with the "ADMIN" role are allowed to access this information. If the user making the request does not have the "ADMIN" role, an UnauthorizedAccessException is thrown.
	 *
	 * @param authorization the authorization header containing the user's access token
	 * @return a list of Quiz objects representing the details of all the quizzes
	 * @throws UnauthorizedAccessException if the user making the request does not have the "ADMIN" role
	 */
	@GetMapping("/getall")
	public List<Quiz> getAllQuizzes(@RequestHeader String authorization) {
		if(authenticationService.validateRole(authorization, "ADMIN")) {
			return this.quizObj.getAllQuizzes();
		}
		else {
			throw new UnauthorizedAccessException();
		}
	}
	
	/**
	 * Retrieves data of a particular quiz by its quiz ID.
	 *
	 * This method fetches the details of a specific quiz identified by the given quiz ID. The user making the request must be authenticated and authorized to access the quiz. If the user is unauthorized or the quiz does not exist, an UnauthorizedAccessException or ResourceNotFoundException is thrown, respectively.
	 *
	 * @param authorization the authorization header containing the user's access token
	 * @param quizId the ID of the quiz to retrieve
	 * @return the Quiz object representing the details of the quiz
	 * @throws JsonProcessingException if an error occurs during JSON processing
	 * @throws UnauthorizedAccessException if the user making the request is unauthorized to access the quiz
	 * @throws ResourceNotFoundException if the quiz with the specified ID does not exist
	 */	
	@GetMapping("/getquiz")
	public Quiz getQuiz(@RequestHeader String authorization, @RequestParam Long quizId) throws JsonProcessingException {
		
		User user = authenticationService.validateRequest(authorization);
		
		return this.quizObj.getQuiz(quizId, user);
	}
	
	/**
	 * Creates a new quiz and returns its details.
	 *
	 * This method allows an authorized admin user to create a new quiz by providing the quiz details in the request body. The created quiz is then saved in the database, and its details, including the generated quiz ID, are returned. If the user making the request is unauthorized, an UnauthorizedAccessException is thrown.
	 *
	 * @param quiz the Quiz object representing the details of the quiz to create
	 * @param authorization the authorization header containing the user's access token
	 * @return the Quiz object representing the details of the created quiz, including the quiz ID
	 * @throws UnauthorizedAccessException if the user making the request is unauthorized to create quizzes
	 */
	@PostMapping("/create")
	public Quiz createQuiz(@Valid @RequestBody Quiz quiz, @RequestHeader String authorization){
		if(authenticationService.validateRole(authorization, "ADMIN")) {
			return this.quizObj.createQuiz(quiz);
		}
		else {
			throw new UnauthorizedAccessException();
		}
	}
	
	/**
	 * Submits a quiz for a user and returns the submission details.
	 *
	 * This method allows an authorized user to submit a quiz by providing the quiz ID and a list of submitted questions in the request body. The user's access token is used to authenticate the request and associate the submission with the user. If the user is unauthorized or has already attempted the quiz, an UnauthorizedAccessException is thrown. If the quiz ID is invalid, a ResourceNotFoundException is thrown. Upon successful submission, the user's score and submission details are returned.
	 *
	 * @param authorization the authorization header containing the user's access token
	 * @param quizId the ID of the quiz to submit
	 * @param submission the list of Questions representing the user's submitted answers
	 * @return the Submissions object representing the user's submission details, including the score
	 * @throws UnauthorizedAccessException if the user making the request is unauthorized to submit the quiz or has already attempted it
	 * @throws ResourceNotFoundException if the quiz ID is invalid and the quiz doesn't exist
	 */
	@PostMapping("/submit")
	public Submissions submitQuiz(@RequestHeader String authorization, @RequestParam Long quizId, @Valid @RequestBody List<Questions> submission) {
		User user = authenticationService.validateRequest(authorization);
		
		System.out.println(user);
		
		if(user.roleType.equals("USER")) {
			System.out.println("ok");
			long userId = user.userId;
			
			quizObj.checkIfQuizExists(quizId);

			Submissions userSubmission = this.submissionsServiceObj.getSubmissionsByQuizAndUserId(quizId, userId);
			if(userSubmission != null)
				throw new UnauthorizedAccessException("You have already attempted the quiz");

			long score = this.quizObj.submitQuiz(submission);
			Quiz quiz = new Quiz();
			quiz.setQuizId(quizId);
			Submissions quizSubmission = new Submissions(score, userId, quiz);		
			return submissionsServiceObj.submitQuizResults(quizSubmission);
		}
		else {
			System.out.println("err");
			throw new UnauthorizedAccessException();
		}
		
	}
	
	/**
	 * Updates an existing quiz and returns the updated quiz details.
	 *
	 * This method allows an authorized user with the role of ADMIN to update the details of an existing quiz. The updated quiz object is provided in the request body. The user's access token is used to authenticate the request. If the user is unauthorized, an UnauthorizedAccessException is thrown. Upon successful update, the updated quiz details are returned.
	 *
	 * @param authorization the authorization header containing the user's access token
	 * @param quiz the updated Quiz object representing the modified quiz details
	 * @return the Quiz object representing the updated quiz details
	 * @throws UnauthorizedAccessException if the user making the request is unauthorized to update quizzes
	 */
	@PutMapping("/update")
	public Quiz updateQuiz(@RequestHeader String authorization, @Valid @RequestBody Quiz quiz){
		if(authenticationService.validateRole(authorization, "ADMIN")) {
			return this.quizObj.UpdateQuiz(quiz);
		}
		else {
			throw new UnauthorizedAccessException();
		}
		
	}
	
	/**
	 * Deletes a quiz based on the provided quiz ID.
	 *
	 * This method allows an authorized user with the role of ADMIN to delete a quiz by specifying the quiz ID. The user's access token is used to authenticate the request. If the user is unauthorized, an UnauthorizedAccessException is thrown. Upon successful deletion, a ResponseEntity with a success message is returned.
	 *
	 * @param authorization the authorization header containing the user's access token
	 * @param quizId the ID of the quiz to be deleted
	 * @return a ResponseEntity with a success message indicating that the quiz was deleted successfully
	 * @throws UnauthorizedAccessException if the user making the request is unauthorized to delete quizzes
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteQuiz(@RequestHeader String authorization, @RequestParam Long quizId){
		if(authenticationService.validateRole(authorization, "ADMIN")) {
			quizObj.deleteQuiz(quizId);
			return ResponseEntity.ok().body("Quiz deleted sucessfully");
		}
		else {
			throw new UnauthorizedAccessException();
		}
		
	}
	
	/**
	 * Retrieves statistics for the admin user.
	 *
	 * This method allows an authorized user with the role of ADMIN to retrieve statistics related to quizzes. The user's access token is used to authenticate the request. If the user is unauthorized, an UnauthorizedAccessException is thrown. The method returns a map containing different statistics such as the total number of quizzes, the number of quizzes created today, and the number of submissions received today.
	 *
	 * @param authorization the authorization header containing the user's access token
	 * @return a map containing statistics for the admin user, such as the total number of quizzes, the number of quizzes created today, and the number of submissions received today
	 * @throws UnauthorizedAccessException if the user making the request is unauthorized to access the statistics
	 */
	@GetMapping("/admin/getstats")
	public Map<String, Long> getAdminStats(@RequestHeader String authorization){
		if(authenticationService.validateRole(authorization, "ADMIN")) {
			Map<String,Long> res = quizObj.getAdminStats(authorization);
			return res;
		}
		else {
			throw new UnauthorizedAccessException();
		}
	}
	
	/**
	 * Retrieves statistics for the user.
	 *
	 * This method allows an authorized user with the role of USER to retrieve statistics related to their own quiz performance. The user's access token is used to authenticate the request. If the user is unauthorized or has a role other than USER, an UnauthorizedAccessException is thrown. The method returns a map containing different statistics such as the total number of quizzes attempted by the user, the number of quizzes attempted today, and the user's overall score.
	 *
	 * @param authorization the authorization header containing the user's access token
	 * @return a map containing statistics for the user, such as the total number of quizzes attempted, the number of quizzes attempted today, and the user's overall score
	 * @throws UnauthorizedAccessException if the user making the request is unauthorized to access the statistics
	 */
	@GetMapping("/user/getstats")
	public Map<String, Long> getUserStats(@RequestHeader String authorization){
		User user = authenticationService.validateRequest(authorization);

		if(user.roleType.equals("USER")) {
			Map<String,Long> res = quizObj.getUserStats(authorization, user.getUserId());
			return res;
		}
		else {
			throw new UnauthorizedAccessException();
		}
	}
}
