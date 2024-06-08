package com.quizapp.quiz.services;

import com.quizapp.quiz.dao.QuizRepository;
import com.quizapp.quiz.entities.Questions;
import com.quizapp.quiz.entities.Quiz;
import com.quizapp.quiz.entities.Submissions;
import com.quizapp.quiz.entities.User;
import com.quizapp.quiz.exceptions.ResourceNotFoundException;
import com.quizapp.quiz.exceptions.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionsService questionsService;

    @Mock
    private SubmissionsService submissionsService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private QuizServiceImpl quizService;

    private Quiz quiz;
    private User user;
    private Submissions submission;

    @BeforeEach
    void setUp() {
        quiz = new Quiz(1L, "Sample Quiz", true, 10L, new ArrayList<>(), new ArrayList<>());
        user = new User();
        user.setUserId(1);
        user.setRole("USER");

        submission = new Submissions();
        submission.setSubmissionId(1L);
        submission.setUserId(1L);
        submission.setQuiz(quiz);
    }

    @Test
    void testGetQuiz_ActiveQuiz_UserNotAttempted() {
        when(quizRepository.findByQuizId(1L)).thenReturn(Optional.of(quiz));
        when(submissionsService.getSubmissionsByQuizAndUserId(1L, 1L)).thenReturn(null);

        Quiz retrievedQuiz = quizService.getQuiz(1L, user);

        assertNotNull(retrievedQuiz);
        assertEquals(quiz.getQuizId(), retrievedQuiz.getQuizId());
        verify(quizRepository, times(1)).findByQuizId(1L);
        verify(submissionsService, times(1)).getSubmissionsByQuizAndUserId(1L, 1L);
    }

    @Test
    void testGetQuiz_QuizNotFound() {
        when(quizRepository.findByQuizId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            quizService.getQuiz(1L, user);
        });

        verify(quizRepository, times(1)).findByQuizId(1L);
    }

    @Test
    void testGetQuiz_QuizNotActive() {
        quiz.setQuizActive(false);
        when(quizRepository.findByQuizId(1L)).thenReturn(Optional.of(quiz));

        assertThrows(ResourceNotFoundException.class, () -> {
            quizService.getQuiz(1L, user);
        });

        verify(quizRepository, times(1)).findByQuizId(1L);
    }

    @Test
    void testGetQuiz_UserAlreadyAttempted() {
        when(quizRepository.findByQuizId(1L)).thenReturn(Optional.of(quiz));
        when(submissionsService.getSubmissionsByQuizAndUserId(1L, 1L)).thenReturn(submission);

        assertThrows(UnauthorizedAccessException.class, () -> {
            quizService.getQuiz(1L, user);
        });

        verify(quizRepository, times(1)).findByQuizId(1L);
        verify(submissionsService, times(1)).getSubmissionsByQuizAndUserId(1L, 1L);
    }

    @Test
    void testCreateQuiz() {
        when(quizRepository.save(quiz)).thenReturn(quiz);

        Quiz createdQuiz = quizService.createQuiz(quiz);

        assertNotNull(createdQuiz);
        assertEquals(quiz.getQuizId(), createdQuiz.getQuizId());
        verify(quizRepository, times(1)).save(quiz);
    }

    @Test
    void testUpdateQuiz() {
        when(quizRepository.findByQuizId(quiz.getQuizId())).thenReturn(Optional.of(quiz));
        when(quizRepository.save(quiz)).thenReturn(quiz);

        Quiz updatedQuiz = quizService.UpdateQuiz(quiz);

        assertNotNull(updatedQuiz);
        assertEquals(quiz.getQuizId(), updatedQuiz.getQuizId());
        verify(quizRepository, times(1)).findByQuizId(quiz.getQuizId());
        verify(quizRepository, times(1)).save(quiz);
    }

    @Test
    void testGetAllQuizzes() {
        List<Quiz> quizzes = Arrays.asList(quiz);
        when(quizRepository.findAll()).thenReturn(quizzes);

        List<Quiz> retrievedQuizzes = quizService.getAllQuizzes();

        assertEquals(1, retrievedQuizzes.size());
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void testSubmitQuiz() {
        Questions question = new Questions();
        question.setQuestionId(1L);
        question.setSubmittedAnswer("Answer");

        List<Questions> submissionList = Arrays.asList(question);
        when(questionsService.isAnswerCorrect(any(Questions.class))).thenReturn(true);

        long score = quizService.submitQuiz(submissionList);

        assertEquals(1, score);
        verify(questionsService, times(1)).isAnswerCorrect(any(Questions.class));
    }

    @Test
    void testDeleteQuiz() {
        when(quizRepository.findByQuizId(1L)).thenReturn(Optional.of(quiz));

        quizService.deleteQuiz(1L);

        verify(quizRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCheckIfQuizExists() {
        when(quizRepository.findByQuizId(1L)).thenReturn(Optional.of(quiz));

        boolean exists = quizService.checkIfQuizExists(1L);

        assertTrue(exists);
        verify(quizRepository, times(1)).findByQuizId(1L);
    }

    @Test
    void testGetAdminStats() {
        when(quizRepository.count()).thenReturn(10L);
        when(submissionsService.getSubmissionsCount()).thenReturn(100L);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer token");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());

        ResponseEntity<List> responseEntity = ResponseEntity.ok(users);

        when(restTemplate.exchange("https://users-service.cfapps.us10-001.hana.ondemand.com/users/getall", HttpMethod.GET, entity, List.class))
                .thenReturn(responseEntity);

        Map<String, Long> stats = quizService.getAdminStats("Bearer token");

        assertEquals(10L, stats.get("quizCount"));
        assertEquals(100L, stats.get("submissionsCount"));
        assertEquals(1L, stats.get("usersCount")); // because we subtract 1 as per the implementation

        verify(quizRepository, times(1)).count();
        verify(submissionsService, times(1)).getSubmissionsCount();
        verify(restTemplate, times(1)).exchange("https://users-service.cfapps.us10-001.hana.ondemand.com/users/getall", HttpMethod.GET, entity, List.class);
    }

    @Test
    void testGetUserStats() {
        when(submissionsService.getUserSubmissionsCount(1)).thenReturn(10L);

        Map<String, Long> stats = quizService.getUserStats("Bearer token", 1);

        assertEquals(10L, stats.get("submissionsCount"));
        verify(submissionsService, times(1)).getUserSubmissionsCount(1);
    }
}
