package com.captcha.repo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.captcha.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

	boolean existsByEmail(String email);
	
	@Transactional
	@Modifying
	@Query(value="update user set s_value=?2 where display_name=?1",nativeQuery = true)
	int updateUserData(String unsername, int count);

	@Query(value="select s_value from user where display_name=?1",nativeQuery = true)
	int getCount(String username);
	
	@Transactional
	@Modifying
	@Query(value="update user set s_value='0'",nativeQuery = true)
	int updateUserDataJob();
	
	@Query(value="select refund_amount from user where display_name=?1",nativeQuery = true)
	int getAmount(String username);
	
	@Transactional
	@Modifying
	@Query(value="update user set refund_amount=?2 where display_name=?1",nativeQuery = true)
	int updateUserAmount(String unsername, int amount);
	
}
