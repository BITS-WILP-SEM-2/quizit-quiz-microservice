package com.quizapp.quiz.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="submissions")
public class Submissions {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long submissionId;
	
	@NotNull(message = "Total correct is required")
	public Long totalCorrect;
	
	@NotNull(message = "User id is required")
	public Long userId;
	
	@ManyToOne
	@JoinColumn(name = "quiz_id")
	@JsonIgnore
	public Quiz quiz;
	
	@Transient
	public User user;

	public Submissions(Long totalCorrect, Long userId, Quiz quiz) {
		super();
		this.totalCorrect = totalCorrect;
		this.userId = userId;
		this.quiz = quiz;
	}

	public Submissions() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}

	public Long getTotalCorrect() {
		return totalCorrect;
	}

	public void setTotalCorrect(Long totalCorrect) {
		this.totalCorrect = totalCorrect;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Quiz getQuiz() {
		return quiz;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Submissions [submissionId=" + submissionId + ", totalCorrect=" + totalCorrect + ", userId=" + userId
				+ ", quiz=" + quiz + ", user=" + user + "]";
	}
}
