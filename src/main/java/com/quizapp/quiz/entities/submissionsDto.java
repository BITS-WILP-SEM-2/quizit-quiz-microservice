package com.quizapp.quiz.entities;

public class submissionsDto {

	public Long submissionId;
	public Long totalCorrect;
	public User user;
	
	public submissionsDto(Long submissionId, Long totalCorrect, User user) {
		super();
		this.submissionId = submissionId;
		this.totalCorrect = totalCorrect;
		this.user = user;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "submissionsDto [submissionId=" + submissionId + ", totalCorrect=" + totalCorrect + ", user=" + user
				+ "]";
	}

	public submissionsDto() {
		super();
		// TODO Auto-generated constructor stub
	}	
	
}
