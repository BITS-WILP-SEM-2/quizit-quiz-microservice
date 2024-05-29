package com.quizapp.quiz.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name="quiz")
public class Quiz {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long quizId;
	
	@NotEmpty(message = "Quiz name is required")
	@Size(min = 4, max = 60, message = "Quiz name should have minimum 4 and maximum 60 characters")
	public String quizName;
	
	@NotNull(message = "Quiz active status is required")
	public boolean quizActive;
	
	@NotNull(message = "Quiz should have atleast 1 question")
	public long totalQuestions;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="quiz_id")
	public List<Questions> questions;
	
	@OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
	@JsonIgnore
	public List<Submissions> submission;

	public Quiz(long quizId, String quizName, boolean quizActive, long totalQuestions, List<Questions> questions,
			List<Submissions> submission) {
		super();
		this.quizId = quizId;
		this.quizName = quizName;
		this.quizActive = quizActive;
		this.totalQuestions = totalQuestions;
		this.questions = questions;
		this.submission = submission;
	}

	public Quiz() {
		super();
		// TODO Auto-generated constructor stub
	}

	public long getQuizId() {
		return quizId;
	}

	public void setQuizId(long quizId) {
		this.quizId = quizId;
	}

	public String getQuizName() {
		return quizName;
	}

	public void setQuizName(String quizName) {
		this.quizName = quizName;
	}

	public boolean isQuizActive() {
		return quizActive;
	}

	public void setQuizActive(boolean quizActive) {
		this.quizActive = quizActive;
	}

	public long getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(long totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public List<Questions> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Questions> questions) {
		this.questions = questions;
	}

	public List<Submissions> getSubmission() {
		return submission;
	}

	public void setSubmission(List<Submissions> submission) {
		this.submission = submission;
	}

	@Override
	public String toString() {
		return "Quiz [quizId=" + quizId + ", quizName=" + quizName + ", quizActive=" + quizActive + ", totalQuestions="
				+ totalQuestions + ", questions=" + questions + ", submission=" + submission + "]";
	}
}
