package com.captcha.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class DemoPageController {
private static final String LOCATION = "Location";
	
	@Value("${url}")
	private String url;
	
	@GetMapping(value = "/login") 
	public void redirect1(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	
	@GetMapping(value = "/captcha-page") 
	public void redirect2(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	
	@GetMapping(value = "/home") 
	public void redirect3(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	@GetMapping(value = "/order") 
	public void redirect4(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	@GetMapping(value = "/payment") 
	public void redirect5(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	@GetMapping(value = "/history") 
	public void redirect6(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	@GetMapping(value = "/history-wallet") 
	public void redirect7(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	@GetMapping(value = "/register") 
	public void redirect8(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	@GetMapping(value = "/profile") 
	public void redirect9(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	@GetMapping(value = "/demo") 
	public void redirect10(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	@GetMapping(value = "/allhistory") 
	public void redirect11(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	@GetMapping(value = "/allhistory-wallet") 
	public void redirect12(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(LOCATION, url);
		httpServletResponse.setStatus(302);
		
	}
	
}
