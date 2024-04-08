package com.captcha.models;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface RandomStringDao extends JpaRepository<RandomString, String> {

	
	@Query(value="select * from randoms where random_string=?1",nativeQuery = true)
	List<RandomString> getRandomDataById(String randomString);
	
	@Query(value="select max(id) FROM randoms",nativeQuery = true)
	Long getMaxId();
	
	@Modifying
	@Query(value="delete FROM randoms where random_string=?1",nativeQuery = true)
	int deleteCaptach(String value);
}