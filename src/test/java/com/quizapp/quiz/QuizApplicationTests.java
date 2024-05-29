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
	
	@Test
	void testit() {
		int x= 1+1;
		assertThat(x).isEqualTo(2);
	}

}
