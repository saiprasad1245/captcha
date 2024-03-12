package com.captcha.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.captcha.repo.UserRepository;


@Component
public class MyStatusJobScheduler {

	 @Autowired
	 UserRepository userRepo;

	private static final Logger log = LoggerFactory.getLogger(MyStatusJobScheduler.class);

	
	
	//0 0 0 * * * Run the Scheduler EveryDay at 12:00 AM
	@Scheduled(cron = "0 0 0 */7 * *")
	public void scheduleOriginatorJob() {
		log.info("update user data every day at 12:00 AM");
		userRepo.updateUserDataJob();
	}
	
	

	
	
	
	
	
	
	
	
	
	
}
