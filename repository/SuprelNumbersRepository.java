package com.stellantis.SUPREL.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stellantis.SUPREL.model.SuprelNumbers;



@Repository
public interface SuprelNumbersRepository extends JpaRepository<SuprelNumbers, String>{
	
	@Query(value="select * from suprel_numbers t where t.CREATED_ON like concat('%',?1,'%') order by SUPREL_NO desc LIMIT 1",nativeQuery = true)
	List<SuprelNumbers> getClaimRecordsOnCreatedDate(int year);
	
}
