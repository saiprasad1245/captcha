package com.stellantis.SUPREL.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stellantis.SUPREL.model.SuprelManager;
import com.stellantis.SUPREL.repository.UserDetRepository;

@Service
@Transactional(rollbackOn=RuntimeException.class)
public class UserDetailsService {
	
	@Autowired
	private UserDetRepository userDetRepository;
	
	
	public String getRoleById(String id){
		String role = userDetRepository.getRoleById(id) ;
		SuprelManager managerRole = userDetRepository.getManagerRole(id);
		if(role != null && !role.isEmpty()) {
			return role;
		}else if(managerRole != null && managerRole.getManagerTid() != null){
			return "Manager";
		} else {
			return "Originator";
		}
	}
	
	public List<String> getUserIds(){
		return userDetRepository.getUserIds();
	}

}

