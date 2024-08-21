package com.stellantis.SUPREL.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stellantis.SUPREL.model.SuprelManager;
import com.stellantis.SUPREL.model.UserDetails;



@Repository
public interface UserDetRepository extends JpaRepository<UserDetails, String>{
	
	@Query(value="select role from suprel_user where user_id=?1",nativeQuery = true)
	String getRoleById(String id);
	
	@Query(value="select distinct(ORIGINATIOR_NAME) from suprel_master where CREATED_BY is NOT NULL",nativeQuery = true)
	List<String> getUserIds();
	
	@Query("from SuprelManager where managerTid=?1")
	SuprelManager getManagerRole(String id);
}
