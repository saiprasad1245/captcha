package com.captcha.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.captcha.model.Attachments;
import com.captcha.model.Order;

@Repository
public interface PaymentRepository extends JpaRepository<Attachments, Long> {

	@Query("from Attachments where email=?1 and fileName =?2")
	Attachments getAttachments(String email, String fileName);
	
	@Query("from Attachments where name=?1")
	List<Attachments> getAllAttachments(String name);
}
