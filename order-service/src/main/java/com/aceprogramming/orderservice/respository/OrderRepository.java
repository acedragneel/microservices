package com.aceprogramming.orderservice.respository;

import com.aceprogramming.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
