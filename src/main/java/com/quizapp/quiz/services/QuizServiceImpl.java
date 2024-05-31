package com.quizapp.quiz.services;

import com.quizapp.quiz.dao.QuizRepository;
import com.quizapp.quiz.entities.Questions;
import com.quizapp.quiz.entities.Quiz;
import com.quizapp.quiz.entities.Submissions;
import com.quizapp.quiz.entities.User;
import com.quizapp.quiz.exceptions.ResourceNotFoundException;
import com.quizapp.quiz.exceptions.UnauthorizedAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuizRepository quizRepositoryObj;
	
	@Autowired 
	private QuestionsService questionsServiceObj;
	
	@Autowired
	@Lazy
	private SubmissionsService submissionsServiceObj;
	
	@Autowired
	public RestTemplate restTemplate;
	
	/**
	 * Retrieves a quiz by its ID and performs necessary validations based on the user role.
	 * 
	 * @param quizId the ID of the quiz to retrieve
	 * @param user the user accessing the quiz
	 * @return the Quiz object
	 * @throws ResourceNotFoundException if the quiz does not exist or is not active
	 * @throws UnauthorizedAccessException if the user has already attempted the quiz
	 */
	@Override
	public Quiz getQuiz(Long quizId, User user) {

		Quiz quiz = quizRepositoryObj.findByQuizId(quizId).orElseThrow(()-> new ResourceNotFoundException("The quiz you are looking for doesn't exist."));
		if(!quiz.isQuizActive() && user.getRole().equals("USER"))
			throw new ResourceNotFoundException("The quiz you are looking for is no longer active");

		if(user.getRole().equals("USER")) {
			Submissions submission = this.submissionsServiceObj.getSubmissionsByQuizAndUserId(quizId, user.getUserId());
			if(submission != null)
				throw new UnauthorizedAccessException("You have already attempted the quiz");
			List<Questions> filteredQuestions = questionsServiceObj.filterQuestionsForUser(quiz.questions);
			quiz.setQuestions(filteredQuestions);
		}

		return quiz;
	}

	/**
	 * Creates a new quiz.
	 * 
	 * @param quiz the Quiz object to create
	 * @return the created Quiz object
	 */
	@Override
	public Quiz createQuiz(Quiz quiz) {
		return quizRepositoryObj.save(quiz);
	}
	
	/**
	 * Updates an existing quiz.
	 * 
	 * @param quiz the Quiz object to update
	 * @return the updated Quiz object
	 */
	@Override
	public Quiz UpdateQuiz(Quiz quiz) {
		checkIfQuizExists(quiz.getQuizId());
		return quizRepositoryObj.save(quiz);
	}

	/**
	 * Retrieves data of all the quizzes in the database.
	 * 
	 * @return a list of Quiz objects representing all the quizzes
	 */
	@Override
	public List<Quiz> getAllQuizzes() {
		return quizRepositoryObj.findAll();
	}

	/**
	 * Submits a quiz and calculates the score based on the submitted answers.
	 * 
	 * @param submission the list of Questions objects representing the submitted answers
	 * @return the score achieved by the user
	 */
	@Override
	public long submitQuiz(List<Questions> submission) {
		long score=0l;
		for(Questions entry : submission) {
			if(questionsServiceObj.isAnswerCorrect(entry)) {
				score++;
			}
		}
		return score;
	}

	/**
	 * Deletes a quiz from the database.
	 * 
	 * @param quizId the ID of the quiz to be deleted
	 * @throws ResourceNotFoundException if the quiz does not exist
	 */
	@Override
	public void deleteQuiz(Long quizId) {
		checkIfQuizExists(quizId);
		quizRepositoryObj.deleteById(quizId);
	}

	/**
	 * Checks if a quiz with the given ID exists in the database.
	 * 
	 * @param quizId the ID of the quiz to check
	 * @return true if the quiz exists, false otherwise
	 * @throws ResourceNotFoundException if the quiz does not exist
	 */
	@Override
	public boolean checkIfQuizExists(long quizId) {
		boolean isQuizExists = quizRepositoryObj.findByQuizId(quizId).isPresent();
		if(!isQuizExists)
			throw new ResourceNotFoundException("This quiz doesn't exist");
		return true;
	}
	
	/**
	 * Retrieves the admin statistics, including the total number of quizzes, total number of submissions,
	 * and total number of users.
	 *
	 * @param authorization the authorization token
	 * @return a map containing the statistics
	 */
	@Override
	public Map<String, Long> getAdminStats(String authorization){
		Map<String, Long> res= new HashMap<>();
		Long totalQuiz = quizRepositoryObj.count();
		Long totalSubmissions = submissionsServiceObj.getSubmissionsCount();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authorization);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		@SuppressWarnings("rawtypes")
		ResponseEntity<List> totalUsers= restTemplate.exchange("http://13.50.219.28:8080/users/getall", HttpMethod.GET, entity, List.class);
		res.put("quizCount", totalQuiz);
		res.put("submissionsCount", totalSubmissions);
		res.put("usersCount", (long)totalUsers.getBody().size()-1);
		return res;
	}
	
	/**
	 * Retrieves the user statistics, including the total number of submissions for a specific user.
	 *
	 * @param authorization the authorization token
	 * @param userId the ID of the user
	 * @return a map containing the statistics
	 */
	@Override
	public Map<String, Long> getUserStats(String authorization, Integer userId){
		Map<String, Long> res= new HashMap<>();
		Long totalSubmissions = submissionsServiceObj.getUserSubmissionsCount(userId);
		
		res.put("submissionsCount", totalSubmissions);
		return res;
	}

	public void setQuizRepository(QuizRepository quizRepository) {
		this.quizRepositoryObj = quizRepository;
	}

	public void setQuestionsService(QuestionsService questionsService) {
		this.questionsServiceObj = questionsService;
	}

	public void setSubmissionsService(SubmissionsService submissionsService) {
		this.submissionsServiceObj = submissionsService;
	}

	public void setRestTemplate(RestTemplate restTemplate2) {
		this.restTemplate = restTemplate2;
	}
}
