package com.quizapp.quiz.services;

import com.quizapp.quiz.dao.QuestionsRepository;
import com.quizapp.quiz.entities.Questions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionsServiceImpl implements QuestionsService {

	@Autowired
	private QuestionsRepository QuestionsRepositoryObj;
	
	/**
	 * Retrieves a list of questions with answers for the given quiz ID.
	 * 
	 * @param quizId the ID of the quiz
	 * @return a list of questions with answers
	 */
	@Override
	public List<Questions> getQuestionsWithAnswers(Long quizId) {
		return QuestionsRepositoryObj.getAllWithAnswers(quizId);
	}

	/**
	 * Creates a list of questions and saves them to the database.
	 * 
	 * @param questions the list of questions to be created
	 * @return the list of created questions
	 */
	@Override
	public List<Questions> createQuestions(List<Questions> questions) {
		return QuestionsRepositoryObj.saveAll(questions);
	}

	/**
	 * Retrieves the list of questions without answers for a specific quiz.
	 * 
	 * @param quizId the ID of the quiz
	 * @return the list of questions without answers
	 */
	@Override
	public List<Questions> getQuestionsWithoutAnswers(Long quizId) {
		return QuestionsRepositoryObj.getAllWithoutAnswers(quizId);
	}
	
	/**
	 * Checks if the submitted answer for a question is correct.
	 * 
	 * @param entry the question entry containing the question ID and the submitted answer
	 * @return true if the answer is correct, false otherwise
	 */
	@Override
	public boolean isAnswerCorrect(Questions entry) {
		if(QuestionsRepositoryObj.findByQuestionId(entry.questionId).answer.equalsIgnoreCase(entry.submittedAnswer))
			return true;
		return false;
	}

	/**
	 * Filters the list of questions for a user by removing the answer information.
	 * 
	 * @param questions the list of questions to filter
	 * @return the filtered list of questions without answer information
	 */
	@Override
	public List<Questions> filterQuestionsForUser(List<Questions> questions) {
		for(Questions entry: questions) {
			entry.answer = null;
		}
		return questions;
	}

	public void setQuestionsRepository(QuestionsRepository questionsRepository) {
		// TODO Auto-generated method stub
		this.QuestionsRepositoryObj = questionsRepository;
	}

}
