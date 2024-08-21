package com.stellantis.SUPREL.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stellantis.SUPREL.model.SuprelAuditLog;

@Repository
public interface SuprelAuditLogRepository extends JpaRepository<SuprelAuditLog, String> {
	
	@Query(value="Select * from suprel_audit_log where SUPREL_NO=?1 order by DATE_AND_TIME",nativeQuery = true)
	List<SuprelAuditLog> getAuditLogDetailsForSuprelNum(String suprelNo);

}
