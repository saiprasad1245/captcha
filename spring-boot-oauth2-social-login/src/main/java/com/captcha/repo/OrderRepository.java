package com.captcha.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.captcha.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Order findByRazorpayOrderId(String orderId);
}
