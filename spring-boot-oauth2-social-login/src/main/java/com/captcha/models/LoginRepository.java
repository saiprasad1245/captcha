package com.captcha.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepository extends JpaRepository<LoginData, String>{
	@Query(value="select max(id) FROM login",nativeQuery = true)
	Long getMaxId();
}
