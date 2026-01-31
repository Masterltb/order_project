package com.restaurant.backend.kitchen.service;

import com.restaurant.backend.common.enums.OrderItemStatus;
import com.restaurant.backend.common.enums.OrderStatus;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ResourceNotFoundException;
import com.restaurant.backend.kitchen.dto.KitchenOrderItemResponse;
import com.restaurant.backend.order.entity.Order;
import com.restaurant.backend.order.entity.OrderItem;
import com.restaurant.backend.order.repository.OrderItemRepository;
import com.restaurant.backend.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Kitchen operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class KitchenService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    /**
     * Get pending items for kitchen (PENDING, COOKING).
     */
    public List<KitchenOrderItemResponse> getPendingItems() {
        List<OrderItemStatus> statuses = Arrays.asList(OrderItemStatus.PENDING, OrderItemStatus.COOKING);
        return orderItemRepository.findByStatusInOrderByCreatedAtAsc(statuses).stream()
                .map(KitchenOrderItemResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Update item status to COOKING.
     */
    @Transactional
    public KitchenOrderItemResponse startCooking(Long itemId) {
        log.info("[KITCHEN_START] itemId={}", itemId);

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", itemId));

        if (item.getStatus() != OrderItemStatus.PENDING) {
            throw new BusinessException("Item is not pending");
        }

        item.setStatus(OrderItemStatus.COOKING);
        OrderItem saved = orderItemRepository.save(item);

        log.info("[KITCHEN_COOKING] itemId={}", saved.getId());
        return KitchenOrderItemResponse.from(saved);
    }

    /**
     * Update item status to DONE.
     */
    @Transactional
    public KitchenOrderItemResponse markDone(Long itemId) {
        log.info("[KITCHEN_DONE] itemId={}", itemId);

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", itemId));

        if (item.getStatus() != OrderItemStatus.COOKING) {
            throw new BusinessException("Item is not cooking");
        }

        item.setStatus(OrderItemStatus.DONE);
        OrderItem saved = orderItemRepository.save(item);

        // Check if all items in order are done
        checkAndUpdateOrderStatus(item.getOrder().getId());

        log.info("[KITCHEN_ITEM_DONE] itemId={}", saved.getId());
        return KitchenOrderItemResponse.from(saved);
    }

    /**
     * Update item status to SERVED.
     */
    @Transactional
    public KitchenOrderItemResponse markServed(Long itemId) {
        log.info("[KITCHEN_SERVED] itemId={}", itemId);

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", itemId));

        if (item.getStatus() != OrderItemStatus.DONE) {
            throw new BusinessException("Item is not done");
        }

        item.setStatus(OrderItemStatus.SERVED);
        OrderItem saved = orderItemRepository.save(item);

        log.info("[KITCHEN_ITEM_SERVED] itemId={}", saved.getId());
        return KitchenOrderItemResponse.from(saved);
    }

    private void checkAndUpdateOrderStatus(Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId).orElse(null);
        if (order == null)
            return;

        boolean allDone = order.getItems().stream()
                .allMatch(item -> item.getStatus() == OrderItemStatus.DONE ||
                        item.getStatus() == OrderItemStatus.SERVED ||
                        item.getStatus() == OrderItemStatus.CANCELLED);

        if (allDone && order.getStatus() == OrderStatus.COOKING) {
            order.setStatus(OrderStatus.DONE);
            orderRepository.save(order);
            log.info("[ORDER_DONE] orderId={}", orderId);
        }
    }
}
