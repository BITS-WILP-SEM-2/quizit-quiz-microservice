package com.quizapp.quiz.entities;

public class User {

	public int userId;
	public String name;
	public String email;
	public String roleType;
	
	public User(int userId, String name, String email, String roleType) {
		super();
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.roleType = roleType;
	}

	public User() {
		super();
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return roleType;
	}

	public void setRole(String roleType) {
		this.roleType = roleType;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", name=" + name + ", email=" + email + ", roleType=" + roleType + "]";
	}

}
