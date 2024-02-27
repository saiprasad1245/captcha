package com.captcha.dto;

import lombok.Value;


public class JwtAuthenticationResponse {
	
	public JwtAuthenticationResponse(String accessToken, boolean authenticated, UserInfo user) {
		super();
		this.accessToken = accessToken;
		this.authenticated = authenticated;
		this.user = user;
	}
	private String accessToken;
	private boolean authenticated;
	private UserInfo user;
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public boolean isAuthenticated() {
		return authenticated;
	}
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	public UserInfo getUser() {
		return user;
	}
	public void setUser(UserInfo user) {
		this.user = user;
	}
	
}