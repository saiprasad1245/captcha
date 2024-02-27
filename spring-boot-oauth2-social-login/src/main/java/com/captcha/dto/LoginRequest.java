package com.captcha.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;



public class LoginRequest {
	@NotBlank
	private String email;

	@NotBlank
	private String password;

	

	public LoginRequest(@NotBlank String email, @NotBlank String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}