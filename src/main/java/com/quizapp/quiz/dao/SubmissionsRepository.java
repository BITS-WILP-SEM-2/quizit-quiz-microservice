package com.quizapp.quiz.dao;

import com.quizapp.quiz.entities.Submissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionsRepository extends JpaRepository<Submissions, Long> {

	@Query(value="select * from submissions where quiz_id=:quizId order by total_correct desc", nativeQuery = true)
	List<Submissions> getAllByQuizId(@Param("quizId") Long quizId);

	@Query(value="select * from submissions where quiz_id=:quizId and user_id=:userId", nativeQuery = true)
	Submissions getSubmissionsByQuizAndUserId(@Param("quizId") long quizId, @Param("userId") long userId);

	@Query(value="select * from submissions where user_id=:userId", nativeQuery = true)
	List<Submissions> getSubmissionsByUserId(@Param("userId") Integer userId);
	
}
