package com.quizapp.quiz.services;

import com.quizapp.quiz.dao.QuizRepository;
import com.quizapp.quiz.dao.SubmissionsRepository;
import com.quizapp.quiz.entities.Quiz;
import com.quizapp.quiz.entities.Submissions;
import com.quizapp.quiz.entities.User;
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
class SubmissionsServiceImplTest {

    @Mock
    private SubmissionsRepository submissionsRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private QuizService quizService;

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private SubmissionsServiceImpl submissionsService;

    private Submissions submission;
    private Quiz quiz;
    private User user;

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
        submission.setTotalCorrect(5L);
    }

    @Test
    void testSubmitQuizResults() {
        when(submissionsRepository.save(submission)).thenReturn(submission);

        Submissions savedSubmission = submissionsService.submitQuizResults(submission);

        assertNotNull(savedSubmission);
        assertEquals(submission.getSubmissionId(), savedSubmission.getSubmissionId());
        verify(submissionsRepository, times(1)).save(submission);
    }

    @Test
    void testGetSubmissions() {
        List<Submissions> submissionsList = Arrays.asList(submission);
        when(quizService.checkIfQuizExists(1L)).thenReturn(true);
        when(submissionsRepository.getAllByQuizId(1L)).thenReturn(submissionsList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer token");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<User> responseEntity = ResponseEntity.ok(user);
        when(restTemplate.exchange("https://users-service.cfapps.us10-001.hana.ondemand.com/users?userId=1", HttpMethod.GET, entity, User.class))
                .thenReturn(responseEntity);

        List<Submissions> retrievedSubmissions = submissionsService.getSubmissions(1L, "Bearer token");

        assertEquals(1, retrievedSubmissions.size());
        assertEquals(user, retrievedSubmissions.get(0).getUser());
        verify(quizService, times(1)).checkIfQuizExists(1L);
        verify(submissionsRepository, times(1)).getAllByQuizId(1L);
        verify(restTemplate, times(1)).exchange("https://users-service.cfapps.us10-001.hana.ondemand.com/users?userId=1", HttpMethod.GET, entity, User.class);
    }

    @Test
    void testGetSubmissionsByQuizAndUserId() {
        when(submissionsRepository.getSubmissionsByQuizAndUserId(1L, 1L)).thenReturn(submission);

        Submissions retrievedSubmission = submissionsService.getSubmissionsByQuizAndUserId(1L, 1L);

        assertNotNull(retrievedSubmission);
        assertEquals(submission.getSubmissionId(), retrievedSubmission.getSubmissionId());
        verify(submissionsRepository, times(1)).getSubmissionsByQuizAndUserId(1L, 1L);
    }

    @Test
    void testGetSubmissionsCount() {
        when(submissionsRepository.count()).thenReturn(10L);

        Long count = submissionsService.getSubmissionsCount();

        assertEquals(10L, count);
        verify(submissionsRepository, times(1)).count();
    }

    @Test
    void testGetUserSubmissionsCount() {
        List<Submissions> userSubmissions = Arrays.asList(submission);
        when(submissionsRepository.getSubmissionsByUserId(1)).thenReturn(userSubmissions);

        Long count = submissionsService.getUserSubmissionsCount(1);

        assertEquals(1, count);
        verify(submissionsRepository, times(1)).getSubmissionsByUserId(1);
    }

    @Test
    void testGetSubmissionsByUserId() {
        List<Submissions> userSubmissions = Arrays.asList(submission);
        when(submissionsRepository.getSubmissionsByUserId(1)).thenReturn(userSubmissions);
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        List<HashMap<String, String>> response = submissionsService.getSubmissionsByUserId(1);

        assertEquals(1, response.size());
        assertEquals("5", response.get(0).get("score"));
        assertEquals("Sample Quiz", response.get(0).get("quizName"));
        assertEquals("1", response.get(0).get("quizId"));
        verify(submissionsRepository, times(1)).getSubmissionsByUserId(1);
        verify(quizRepository, times(1)).findById(1L);
    }
}
