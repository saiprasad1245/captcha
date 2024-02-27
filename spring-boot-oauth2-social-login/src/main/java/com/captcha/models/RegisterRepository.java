package com.captcha.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.captcha.model.User;

@Repository
public interface RegisterRepository extends JpaRepository<User, String>{

	
	@Query(value="select * from user where email=?1 and password=?2",nativeQuery = true)
	List<User> getByID(String email, String password);
	
	@Query(value="select max(user_id) FROM user",nativeQuery = true)
	Long getMaxId();

	
	
}
