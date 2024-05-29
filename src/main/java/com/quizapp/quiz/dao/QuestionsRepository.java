package com.quizapp.quiz.dao;

import com.quizapp.quiz.entities.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionsRepository extends JpaRepository<Questions, Long> {

	@Query(value="select q.question_id, q.question, q.option1, q.option2, q.option3, q.option4, q.answer from questions as q where q.quiz_id = :quizId", nativeQuery = true)
	public List<Questions> getAllWithAnswers(@Param("quizId") Long quizId);
	
	@Query(value="select q.question_id, q.question, q.option1, q.option2, q.option3, q.option4 from questions as q where q.quiz_id = :quizId", nativeQuery = true)
	public List<Questions> getAllWithoutAnswers(@Param("quizId") Long quizId);

	public Questions findByQuestionId(Long entry);

}
