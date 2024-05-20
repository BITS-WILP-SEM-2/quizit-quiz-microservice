package com.quizapp.quiz.services;

import com.quizapp.quiz.entities.Questions;
import com.quizapp.quiz.entities.Quiz;
import com.quizapp.quiz.entities.User;

import java.util.List;
import java.util.Map;

public interface QuizService {

	Quiz getQuiz(Long quizId, User user);
	
	List<Quiz> getAllQuizzes();
	
	long submitQuiz(List<Questions> submission);

	void deleteQuiz(Long quizId);

	Quiz createQuiz(Quiz quiz);

	Quiz UpdateQuiz(Quiz quiz);
	
	boolean checkIfQuizExists(long quizId);
	
	Map<String, Long> getAdminStats(String authorization);
	
	Map<String, Long> getUserStats(String authorization, Integer userId);

}
