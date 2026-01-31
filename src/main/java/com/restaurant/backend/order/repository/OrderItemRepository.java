package com.restaurant.backend.order.repository;

import com.restaurant.backend.common.enums.OrderItemStatus;
import com.restaurant.backend.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for OrderItem entity.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderIdOrderByCreatedAtAsc(Long orderId);

    List<OrderItem> findByStatusInOrderByCreatedAtAsc(List<OrderItemStatus> statuses);
}
