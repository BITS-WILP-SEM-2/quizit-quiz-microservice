package com.quizapp.quiz;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.quizapp.quiz.dao.QuizRepository;
import com.quizapp.quiz.entities.Quiz;

@SpringBootTest
class QuizApplicationTests {

	@Autowired
	private QuizRepository quizRepo;
	Quiz quiz;

	@Test
	void contextLoads() {
	}
	
	@Test
	void testit() {
		int x= 1+1;
		assertThat(x).isEqualTo(2);
	}
	
	@Test
	public void testFindAll() {
		quiz = new Quiz(1, "javaQuiz12", true, 10, null, null);
		quizRepo.save(quiz);
		List<Quiz> res= quizRepo.findAll();
		assertThat(res.get(0).getQuizName()).isEqualTo(quiz.getQuizName());
	}

}
