package com.quizapp.quiz.dao;

import com.quizapp.quiz.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

	public Optional<Quiz> findByQuizId(Long quizId);
	
}
