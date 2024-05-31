package com.quizapp.quiz.services;

import com.quizapp.quiz.dao.QuizRepository;
import com.quizapp.quiz.dao.SubmissionsRepository;
import com.quizapp.quiz.entities.Quiz;
import com.quizapp.quiz.entities.Submissions;
import com.quizapp.quiz.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SubmissionsServiceImpl implements SubmissionsService {

	@Autowired
	public SubmissionsRepository submissionsRepositoryObj;
	
	@Autowired
	public RestTemplate restTemplate;
	
	@Autowired
	private QuizService quizObj;
	
	@Autowired
	private QuizRepository quizRepositoryObj;
	
	/**
	 * Submits the quiz results and saves the submission.
	 *
	 * @param submission the submission to be saved
	 * @return the saved submission
	 */
	@Override
	public Submissions submitQuizResults(Submissions submission) {
		return submissionsRepositoryObj.save(submission);
	}

	/**
	 * Retrieves the submissions for a specific quiz.
	 *
	 * @param quizId        the ID of the quiz
	 * @param authorization the authorization token
	 * @return the list of submissions for the quiz
	 */
	@Override
	public List<Submissions> getSubmissions(Long quizId, String authorization) {
		quizObj.checkIfQuizExists(quizId);
		
		List<Submissions> submissions = submissionsRepositoryObj.getAllByQuizId(quizId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authorization);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		
		for(Submissions submission : submissions) {
			ResponseEntity<User> user = restTemplate.exchange("http://13.50.219.28:8080/users?userId="+submission.getUserId(), HttpMethod.GET, entity, User.class);
			submission.setUser(user.getBody());
		}
		return submissions;
	}

	/**
	 * Retrieves the submission of a specific quiz and user.
	 *
	 * @param quizId the ID of the quiz
	 * @param userId the ID of the user
	 * @return the submission object if found, null otherwise
	 */
	@Override
	public Submissions getSubmissionsByQuizAndUserId(long quizId, long UserId) {
		return submissionsRepositoryObj.getSubmissionsByQuizAndUserId(quizId, UserId);
	}

	/**
	 * Retrieves the total count of submissions.
	 *
	 * @return the total count of submissions
	 */
	@Override
	public Long getSubmissionsCount() {
		return submissionsRepositoryObj.count();
	}
	
	/**
	 * Retrieves the total count of submissions made by a specific user.
	 *
	 * @param userId the ID of the user
	 * @return the total count of submissions made by the user
	 */
	@Override
	public Long getUserSubmissionsCount(Integer userId) {
		return (long) submissionsRepositoryObj.getSubmissionsByUserId(userId).size();
	}
	
	/**
	 * Retrieves the submissions made by a user identified by their user ID.
	 *
	 * @param userId the ID of the user
	 * @return a list of submissions made by the user, with associated quiz information
	 */
	@Override
	public List<HashMap<String,String>> getSubmissionsByUserId(Integer UserId) {
		List<HashMap<String, String>> response = new ArrayList<>();
		List<Submissions> userSubmissions = submissionsRepositoryObj.getSubmissionsByUserId(UserId);
		for (Submissions submissions : userSubmissions) {
			HashMap<String,String> submissionData = new HashMap<>();
			Quiz quiz = quizRepositoryObj.findById(submissions.getQuiz().getQuizId()).get();
			submissionData.put("score", Long.toString(submissions.getTotalCorrect()));
			submissionData.put("quizName", quiz.getQuizName());
			submissionData.put("quizId", Long.toString(quiz.getQuizId()));
			response.add(submissionData);
		}
		return response;
	}

}
