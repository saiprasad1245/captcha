package com.stellantis.SUPREL.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stellantis.SUPREL.model.UserDetails;
import com.stellantis.SUPREL.service.UserDetailsService;

@RestController
public class UserDetailsController {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsController.class);
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	
	@GetMapping(value="/getUserDetails")
	public List<UserDetails> getUserDetails(HttpServletRequest request){
		LOGGER.info("Fetching Home Page details Started");
		HttpSession session = request.getSession(false);
		String userType = (String)session.getAttribute("userType");
		String userId = (String)session.getAttribute("userName");
		String actualName = (String)session.getAttribute("actualName");
		String familyname = (String)session.getAttribute("familyName");
		String userName = actualName+" "+familyname;
		String email = (String)session.getAttribute("emailId");
		
		//String userType = "workforce"; 
		//String userId = "T3328GS";
		//String userName = "SaiPrasad Goparaju";
		//String email = "saiprasad.goparaju@external.stellantis.com";

		LOGGER.info("user attributes {} {} ", userId,userType);
		List<UserDetails> userDetailsList = new ArrayList<UserDetails>();
		UserDetails userDetails = new UserDetails();
		if(userType.equalsIgnoreCase("workforce")) {
			try {
				LOGGER.info("Role from User Details  Fetch Started");
				String userRole =  userDetailsService.getRoleById(userId);
				userDetails.setRole(userRole);
				userDetails.setModifiedBy(userId);
				userDetails.setUserName(userName);
				userDetails.setEmail(email);
				userDetailsList.add(userDetails);
				return userDetailsList;
			}catch (Exception e){
				userDetails.setModifiedBy(userId);
				userDetailsList.add(userDetails);
				return userDetailsList;
			}
		}else if(userType.equalsIgnoreCase("supplier")) {
			try {
				LOGGER.info("Role from User Details  Fetch Started");
				userDetails.setRole("Supplier");
				userDetails.setModifiedBy(userId);
				userDetails.setUserName(userName);
				userDetails.setEmail(email);
				userDetailsList.add(userDetails);
				return userDetailsList;
			}catch (Exception e){
				userDetails.setModifiedBy(userId);
				userDetailsList.add(userDetails);
				return userDetailsList;
			}
		}
		return userDetailsList;
	}
	
	@GetMapping("/getUserIds")
	public List<String> getUserIds(HttpServletRequest request){
		return userDetailsService.getUserIds();
	}
}
