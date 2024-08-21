package com.stellantis.SUPREL.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stellantis.SUPREL.model.SuprelAttachments;

@Repository
public interface SuprelAttachmentRepository extends JpaRepository<SuprelAttachments, String>{

	@Query("from SuprelAttachments where supplierCode=?1 and fileName =?2 and suprelNo=?3")
	SuprelAttachments getSuprelAttachment(String suppNum, String fileName, String suprelNo);
	
	@Modifying
	@Query(value="delete from suprel_attachments where SUPREL_NO=?1", nativeQuery = true)
	void deleteSuprelAttachments(String suprelNo);
	
	@Modifying
	@Query(value="delete from suprel_attachments where SUPPLIER_CODE=?1 and ATTACHMENT_NAME =?2 and SUPREL_NO=?3", nativeQuery = true)
	void deleteAttachments(String suppNum, String fileName, String suprelNo);

	@Query(value="select a.* from suprel_master s, suprel_attachments a where s.suprel_no = a.suprel_no and a.suprel_no=?1", nativeQuery = true)
	List<SuprelAttachments> getAttachmentsById(String suprelNo);
	
	
	@Query(value="select ATTACHMENT_NAME from suprel_attachments where suprel_no=?1", nativeQuery = true)
	List<String> getAttachmentNames(String suprelNo);
}
