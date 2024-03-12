package com.captcha.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.captcha.config.MockUserUtils;
import com.captcha.dto.LocalUser;
import com.captcha.dto.LoginRequest;
import com.captcha.dto.SignUpRequest;
import com.captcha.dto.SocialProvider;
import com.captcha.exception.UserAlreadyExistAuthenticationException;
import com.captcha.model.User;
import com.captcha.service.UserService;

import dev.samstevens.totp.code.CodeVerifier;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class AuthControllerTest2 {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private CodeVerifier verifier;

	@MockBean
	private PasswordEncoder passwordEncoder;

	private static User user = MockUserUtils.getMockUser("captcha");

	private static ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	public void init() {
		LocalUser localUser = LocalUser.create(user, null, null, null);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(localUser, "secret"));
		Mockito.when(userService.findUserByEmail(Mockito.anyString())).thenReturn(user);
		Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
	}

	@Test
	public void testAuthenticateUser() throws Exception {
		LoginRequest loginRequest = new LoginRequest(user.getEmail(), user.getPassword());
		String json = mapper.writeValueAsString(loginRequest);
		mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.authenticated").value("true")).andExpect(jsonPath("$.accessToken").isNotEmpty());

		// Test when user 2fa is enabled
		user.setUsing2FA(true);
		mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.authenticated").value("false")).andExpect(jsonPath("$.user").doesNotExist());
	}

	@Test
	public void testRegisterUser() throws Exception {
		SignUpRequest signUpRequest = new SignUpRequest("1234", "captcha", user.getEmail(), user.getPassword(), user.getPassword(), SocialProvider.FACEBOOK,1);
		// Test when user provided email already exists in the database
		Mockito.when(userService.registerNewUser(any(SignUpRequest.class))).thenReturn(user);
		String json = mapper.writeValueAsString(signUpRequest);
		mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value("true")).andExpect(jsonPath("$.message").value("User registered successfully"));

		// Test when user provided email already exists in the database
		Mockito.when(userService.registerNewUser(any(SignUpRequest.class))).thenThrow(new UserAlreadyExistAuthenticationException("exists"));
		json = mapper.writeValueAsString(signUpRequest);
		mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.success").value("false")).andExpect(jsonPath("$.message").value("Email Address already in use!"));
	}

	@Test
	public void testVerifyCodeWhenCodeIsNotValid() throws Exception {
		Mockito.when(verifier.isValidCode(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
		String json = mapper.writeValueAsString("443322");
		mockMvc.perform(post("/api/auth/verify").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.success").value("false")).andExpect(jsonPath("$.message").value("Invalid Code!"));
	}

	@Test
	public void testVerifyCodeWhenCodeIsValid() throws Exception {
		Mockito.when(verifier.isValidCode(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		String json = mapper.writeValueAsString("443322");
		mockMvc.perform(post("/api/auth/verify").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.authenticated").value("true")).andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.user").exists());
	}
}
