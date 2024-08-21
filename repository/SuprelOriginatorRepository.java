package com.stellantis.SUPREL.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.stellantis.SUPREL.model.SuprelManager;
import com.stellantis.SUPREL.model.SuprelMaster;
import com.stellantis.SUPREL.model.SuprelSISData;


@Repository
public interface SuprelOriginatorRepository extends JpaRepository<SuprelMaster, String>{
	
	@Query(value="select * from suprel_master t where if(IFNULL(?1,'')!='',t.SUPPLIER_CODE like concat('%',?1,'%'),1=1) and if(IFNULL(?2,'')!='',t.SUPPLIER_NAME like concat('%',?2,'%'),1=1) and"
						+ " (if(IFNULL(?3,'') !='', t.ORIGINATIOR_NAME like concat('%',?3,'%'),1=1))" 
						+ "and if(IFNULL(?4,'') !='',t.CITY like concat('%',?4,'%'),1=1) and"
						+ " if(IFNULL(?5,'') !='',t.COUNTRY like concat('%',?5,'%'),1=1) and if(IFNULL(?6,'') !='',t.STATE like concat('%',?6,'%'),1=1)"
						+ "and if(IFNULL(?7,'') !='',t.SUPREL_NO like concat('%',?7,'%'),1=1) and if(IFNULL(?8,'') !='',t.SUPREL_STATUS like "
						+ "concat ('%',?8,'%'),1=1) and if(IFNULL(?9,'') !='',t.PRIMARY_NON_CONFORMITY=?9,1=1) and if(IFNULL(?10,'') !='',t.ISSUED_DATE>?10 "
						+ " or t.ISSUED_DATE=?10,1=1) and if(IFNULL(?11,'') !='',t.ISSUED_DATE<?11 or t.ISSUED_DATE=?11 ,1=1) and if(IFNULL(?12,'')!='',t.SUPPLIER_ADDRESS like concat('%',?12,'%'),1=1)"
						+ " and if(IFNULL(?13,'')!='',t.TECHNICAL_AREA like concat('%',?13,'%'),1=1) and if(IFNULL(?14,'')!='',t.MANAGER_NAME like concat('%',?14,'%'),1=1) and if(IFNULL(?15,'') !='',t.SECONDARY_NON_CONFORMITY=?15,1=1) "
						+ " and if(IFNULL(?16,'') !='',year(t.CREATED_ON) =?16,1=1)"
						+ "order by t.SUPREL_NO DESC",nativeQuery = true)
	List<SuprelMaster> searchIncidentClaim(String supplierCode, String supplierName, String incidentOriginator, String city, String country, String state, String supplierNo, String supplierStatus,
			String primaryConformity, Date fromDate, Date todate, String address, String technicalArea, String managerName, String secondaryConformity, String year);
	
	@Query(value="select * from suprel_master t where if(IFNULL(?1,'')!='',t.SUPPLIER_CODE like concat('%',?1,'%'),1=1) and if(IFNULL(?2,'')!='',t.SUPPLIER_NAME like concat('%',?2,'%'),1=1) and"
			+ " (if(IFNULL(?3,'') !='', t.ORIGINATIOR_NAME like concat('%',?3,'%'),1=1))" 
			+ "and if(IFNULL(?4,'') !='',t.CITY like concat('%',?4,'%'),1=1) and"
			+ " if(IFNULL(?5,'') !='',t.COUNTRY like concat('%',?5,'%'),1=1) and if(IFNULL(?6,'') !='',t.STATE like concat('%',?6,'%'),1=1)"
			+ "and if(IFNULL(?7,'') !='',t.SUPREL_NO like concat('%',?7,'%'),1=1) and if(IFNULL(?8,'') !='',t.SUPREL_STATUS like "
			+ "concat ('%',?8,'%'),1=1) and if(IFNULL(?9,'') !='',t.PRIMARY_NON_CONFORMITY=?9,1=1) and if(IFNULL(?10,'') !='',t.ISSUED_DATE>?10 "
			+ " or t.ISSUED_DATE=?10,1=1) and if(IFNULL(?11,'') !='',t.ISSUED_DATE<?11 or t.ISSUED_DATE=?11 ,1=1) and if(IFNULL(?12,'')!='',t.SUPPLIER_ADDRESS like concat('%',?12,'%'),1=1)"
			+ " and if(IFNULL(?13,'')!='',t.TECHNICAL_AREA like concat('%',?13,'%'),1=1) and if(IFNULL(?14,'')!='',t.MANAGER_NAME like concat('%',?14,'%'),1=1) and if(IFNULL(?15,'') !='',t.SECONDARY_NON_CONFORMITY=?15,1=1) "
			+ " and if(IFNULL(?16,'') !='',year(t.CREATED_ON) =?16,1=1)"
			+ "order by t.SUPREL_NO DESC",nativeQuery = true)
	List<SuprelMaster> searchIncidentClaimSupplier(String supplierCode, String supplierName, String incidentOriginator, String city, String country, String state, String supplierNo, String supplierStatus,
			String primaryConformity, Date fromDate, Date todate, String address, String technicalArea, String managerName, String secondaryConformity, String year);
	
	
	@Query(value="select * from suprel_master where DATEDIFF(CURRENT_DATE, CREATED_ON)>10 and SUPREL_STATUS in ('Draft')",nativeQuery = true)
	List<SuprelMaster> sendDraftEmailNotification();
	
	@Query(value="select * from suprel_master where DATEDIFF(CURRENT_DATE, CREATED_ON)>10 and MODIFIED_ON is null and SUPREL_STATUS in ('Pending')",nativeQuery = true)
	List<SuprelMaster> sendPendingEmailNotification();
	
	@Query(value="select * from suprel_master where DATEDIFF(CURRENT_DATE, MODIFIED_ON)>10 and MODIFIED_ON is not null and SUPREL_STATUS in ('Pending')",nativeQuery = true)
	List<SuprelMaster> sendPendingEmailNotification1();
	
	@Query(value="select * from suprel_master where DATEDIFF(CURRENT_DATE, MODIFIED_ON)>10 and SUPREL_STATUS in ('Disputed')",nativeQuery = true)
	List<SuprelMaster> sendDisputedEmailNotification();
	
	@Query(value="select * from suprel_master where DATEDIFF(CURRENT_DATE, MODIFIED_ON)>10 and SUPREL_STATUS in ('Issued')",nativeQuery = true)
	List<SuprelMaster> sendIssuedEmailNotification();
	
	@Query(value="select * from suprel_master where DATEDIFF(CURRENT_DATE, MODIFIED_ON)>5 and SUPREL_STATUS in ('Issued')",nativeQuery = true)
	List<SuprelMaster> sendIssuedEmailNotificationForFiveDays();
	
	@Query("from SuprelSISData where supplierCode=?1")
	List<SuprelSISData> getSuprelSISData(String supplierCode);
	
	@Query(value="select distinct country from suprel_sis",nativeQuery = true)
	List<String> getDistinctCountryList();
	
	@Query(value="select distinct state from suprel_sis",nativeQuery = true)
	List<String> getDistinctStateList();
	
	@Query("from SuprelManager where managerName=?1")
	SuprelManager getManagerDetails(String managerName);
	
	@Query("from SuprelManager where managerTid=?1")
	SuprelManager getManagerDetailsTid(String managerTid);
	
	
	@Query(value="select supplierCode from SuprelSupplierMap where supplierId=?1")
	List<String> getSupplierCode(String supplierId);
	
	
	@Query(value="select SUPPLIER_CODE from suprel_master where SUPPLIER_CODE like concat('%',?1,'%')",nativeQuery = true)
	List<String> getSupplierCodes(String supplierCode);
	
	
	@Query(value="select * from suprel_master where SUPPLIER_CODE=?1",nativeQuery = true)
	List<SuprelMaster> getSupplierDetails(String supplierManufCodeList);
	
	@Query(value="select * from suprel_master where SUPREL_STATUS in ('Pending', 'Disputed') and MANAGER_NAME=?1 order by SUPREL_NO DESC",nativeQuery = true)
	List<SuprelMaster> pendingCertification(String managerName);
	
	@Transactional
	@Modifying
	@Query(value="update suprel_master set SUPREL_STATUS='Final' , MODIFIED_BY='StatusJob' , MODIFIED_ON=CURRENT_DATE where SUPREL_NO=?1", nativeQuery = true)
	void updateStatus(String suprelNo);
	
	@Query(value="select group_concat(CONTACT_E_MAIL separator ', ') from suprel_supplier_map " + 
			"where SUPPLIER_MANUF_CODE = CONCAT(SUBSTRING(?1,1,5),'','**')",nativeQuery=true)
	String getEmailDetails(String supplierCode);
	
	
	@Query(value="select group_concat(CONTACT_E_MAIL separator ', ') from "
			+ "suprel_supplier_map where SUPPLIER_MANUF_CODE = ?1 ",nativeQuery=true)
	String getEmailDetailsForScode(String supplierCode);

	
	@Query(value="select distinct(state) from suprel_sis where state is not null and state !='' ",nativeQuery = true)
	List<String> getStateList();
	
	@Query(value="select distinct(country) from suprel_sis where country is not null and country !='' ",nativeQuery = true)
	List<String> getCountryList();
	
	@Query(value="select distinct year(created_on) from suprel_master ",nativeQuery = true)
	List<String> getYearList();
	
	
	@Transactional
	@Modifying
	@Query(value="update suprel_master set MAIL_STATUS=?2 where SUPREL_NO=?1", nativeQuery = true)
	void updateMailStatus(String suprelNo, String mailStatus);
	
	
	@Query(value="select * from suprel_master where MANAGER_NAME=?1 and MAIL_STATUS=?2",nativeQuery = true)
	List<SuprelMaster> getSuprelMasterDetails(String managerName, String mailStatus);
	
	
}


