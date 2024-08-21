package com.stellantis.SUPREL.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stellantis.SUPREL.model.TechnicalAreaDetails;


@Repository
public interface TechnicalAreaDetailsRepository extends JpaRepository<TechnicalAreaDetails, String>{

	@Query(value="select distinct TECHNICAL_AREA from Technical_Area_Details",nativeQuery = true)
	List<String> getDistinctTechnicalAreaList();
}
