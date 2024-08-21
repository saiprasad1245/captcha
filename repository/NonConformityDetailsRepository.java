package com.stellantis.SUPREL.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stellantis.SUPREL.model.NonConformityDetails;

@Repository
public interface NonConformityDetailsRepository extends JpaRepository<NonConformityDetails, String>{
	
	@Query(value="select distinct primary_conformity from Non_Conformity_Details",nativeQuery = true)
	List<String> getDistinctPrimaryNonConfList();
	
	@Query(value="select distinct secondary_conformity from Non_Conformity_Details where primary_conformity=?1",nativeQuery = true)
	List<String> getDistinctSecondaryNonConfList(String primaryConf);

}
