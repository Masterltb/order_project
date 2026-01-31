package com.restaurant.backend.order.repository;

import com.restaurant.backend.common.enums.OrderStatus;
import com.restaurant.backend.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Order entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    List<Order> findByTableIdAndIsClosedFalseOrderByCreatedAtDesc(Long tableId);

    List<Order> findByIsClosedFalseOrderByCreatedAtDesc();

    List<Order> findByStatusAndIsClosedFalseOrderByCreatedAtAsc(OrderStatus status);

    boolean existsByTableIdAndIsClosedFalse(Long tableId);

    @Query("SELECT MAX(CAST(SUBSTRING(o.orderNumber, 4) AS int)) FROM Order o WHERE o.orderNumber LIKE :prefix%")
    Integer findMaxOrderNumberByPrefix(@Param("prefix") String prefix);
}
