package com.captcha.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.captcha.model.Attachments;
import com.captcha.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Order findByRazorpayOrderId(String orderId);
	
	@Query("from Order where userName=?1")
	List<Order> getAllAttachments(String name);
}
