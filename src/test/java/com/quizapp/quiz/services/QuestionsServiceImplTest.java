package com.quizapp.quiz.services;

import com.quizapp.quiz.dao.QuestionsRepository;
import com.quizapp.quiz.entities.Questions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionsServiceImplTest {

    @Mock
    private QuestionsRepository questionsRepository;

    @InjectMocks
    private QuestionsServiceImpl questionsService;

    private Questions question1;
    private Questions question2;

    @BeforeEach
    void setUp() {
        question1 = new Questions(1L, "Question 1", "Option 1", "Option 2", "Option 3", "Option 4", "Answer 1", null);
        question2 = new Questions(2L, "Question 2", "Option 1", "Option 2", "Option 3", "Option 4", "Answer 2", null);
    }

    @Test
    void testGetQuestionsWithAnswers() {
        when(questionsRepository.getAllWithAnswers(1L)).thenReturn(Arrays.asList(question1, question2));

        List<Questions> questions = questionsService.getQuestionsWithAnswers(1L);

        assertEquals(2, questions.size());
        assertEquals("Answer 1", questions.get(0).getAnswer());
        assertEquals("Answer 2", questions.get(1).getAnswer());

        verify(questionsRepository, times(1)).getAllWithAnswers(1L);
    }

    @Test
    void testCreateQuestions() {
        List<Questions> questionsToCreate = Arrays.asList(question1, question2);

        when(questionsRepository.saveAll(questionsToCreate)).thenReturn(questionsToCreate);

        List<Questions> createdQuestions = questionsService.createQuestions(questionsToCreate);

        assertEquals(2, createdQuestions.size());
        verify(questionsRepository, times(1)).saveAll(questionsToCreate);
    }

    @Test
    void testGetQuestionsWithoutAnswers() {
        when(questionsRepository.getAllWithoutAnswers(1L)).thenReturn(Arrays.asList(question1, question2));

        List<Questions> questions = questionsService.getQuestionsWithoutAnswers(1L);

        assertEquals(2, questions.size());
        verify(questionsRepository, times(1)).getAllWithoutAnswers(1L);
    }

    @Test
    void testIsAnswerCorrect() {
        Questions entry = new Questions();
        entry.setQuestionId(1L);
        entry.setSubmittedAnswer("Answer 1");

        when(questionsRepository.findByQuestionId(1L)).thenReturn(question1);

        boolean result = questionsService.isAnswerCorrect(entry);

        assertTrue(result);
        verify(questionsRepository, times(1)).findByQuestionId(1L);
    }

    @Test
    void testIsAnswerCorrect_WrongAnswer() {
        Questions entry = new Questions();
        entry.setQuestionId(1L);
        entry.setSubmittedAnswer("Wrong Answer");

        when(questionsRepository.findByQuestionId(1L)).thenReturn(question1);

        boolean result = questionsService.isAnswerCorrect(entry);

        assertFalse(result);
        verify(questionsRepository, times(1)).findByQuestionId(1L);
    }

    @Test
    void testFilterQuestionsForUser() {
        List<Questions> questionsToFilter = Arrays.asList(question1, question2);

        List<Questions> filteredQuestions = questionsService.filterQuestionsForUser(questionsToFilter);

        assertEquals(2, filteredQuestions.size());
        assertNull(filteredQuestions.get(0).getAnswer());
        assertNull(filteredQuestions.get(1).getAnswer());
    }
}
