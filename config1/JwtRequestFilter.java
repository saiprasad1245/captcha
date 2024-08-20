package com.stellantis.SUPREL.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	JwtTokenUtil jwtTokenUtil;

	private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Object username = null;
		Claims claims;
		final String authorizationHeader = request.getHeader("x-amzn-oidc-data");
		logger.info("Getting AuthorizationHeader From Request Header =====>> " + authorizationHeader);
		
		  //HttpSession session = request.getSession();
		  //session.setAttribute("userName", "T9435HV");
		  //session.setAttribute("userType", "Manager");
		  //session.setAttribute("actualName", "Saiprasad");
		  //session.setAttribute("familyName", "Goparaju");
		  //session.setAttribute("emailId", "saiprasad.goparaju@external.stellantis.com");
		 
		  try {
			  if (authorizationHeader != null) {
				  logger.info("Checking AuthorizationHeader Values =====>> " + authorizationHeader);

				  claims = jwtTokenUtil.decode(authorizationHeader);
				  String subject = claims.getSubject();
				  logger.info("subject +++" + subject.toString());
				  username = claims.get("sub");
				  HttpSession session = request.getSession();
				  session.setAttribute("userName", (String)claims.get("sub"));
				  session.setAttribute("userType", (String)claims.get("user_type"));
				  session.setAttribute("actualName", (String)claims.get("given_name"));
				  session.setAttribute("familyName", (String)claims.get("family_name"));
				  session.setAttribute("emailId", (String)claims.get("email"));
				  logger.info("name +++" + username);
				  logger.info("Getting JWT From decodeJwt =====>> " + username);

				  if (claims.get("sub") != null) {
					  logger.info("Getting Sub From Claims =====>> " + claims.get("sub"));
					  logger.info("Enter Into SetUpSpringAuthentication Method To Set Authentication---");
					  setUpSpringAuthentication(claims);
					  logger.info("Authentication Is Sussess");
				  } else {
					  SecurityContextHolder.clearContext();
				  } 

				  //
			  }
			  filterChain.doFilter(request, response);
		  } catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e);
		}

	} 	

	private void setUpSpringAuthentication(Claims claims) {

		List<String> authorities = new ArrayList<>();

		for (Entry<String, Object> entry : claims.entrySet()) {
			authorities.add(entry.getValue().toString());
			logger.info("Getting Key From EntrySet =====>> " + entry.getKey().toString());
			logger.info("Getting Values From EntrySet =====>> " + entry.getValue().toString());
		}

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
				authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		logger.info("Getting getAuthorities =====>> " + auth.getAuthorities().toString());
		logger.info("Getting entry =====>> " + auth.getPrincipal().toString());
		SecurityContextHolder.getContext().setAuthentication(auth);

	}
	
}