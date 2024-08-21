package com.stellantis.SUPREL.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stellantis.SUPREL.model.SuprelManager;



@Repository
public interface ManagerRepository extends JpaRepository<SuprelManager, String>{
	
	
}
