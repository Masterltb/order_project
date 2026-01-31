package com.restaurant.backend.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.backend.admin.table.entity.DiningTable;
import com.restaurant.backend.admin.table.repository.DiningTableRepository;
import com.restaurant.backend.admin.user.entity.User;
import com.restaurant.backend.admin.user.repository.UserRepository;
import com.restaurant.backend.common.enums.OrderItemStatus;
import com.restaurant.backend.common.enums.OrderStatus;
import com.restaurant.backend.common.enums.TableStatus;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ConflictException;
import com.restaurant.backend.common.exception.ResourceNotFoundException;
import com.restaurant.backend.menu.entity.MenuItem;
import com.restaurant.backend.menu.entity.Option;
import com.restaurant.backend.menu.repository.MenuItemRepository;
import com.restaurant.backend.menu.repository.OptionRepository;
import com.restaurant.backend.order.dto.AddOrderItemRequest;
import com.restaurant.backend.order.dto.CreateOrderRequest;
import com.restaurant.backend.order.dto.OrderResponse;
import com.restaurant.backend.order.entity.Order;
import com.restaurant.backend.order.entity.OrderItem;
import com.restaurant.backend.order.repository.OrderItemRepository;
import com.restaurant.backend.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for Order management.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DiningTableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Get all open orders.
     */
    public List<OrderResponse> getOpenOrders() {
        return orderRepository.findByIsClosedFalseOrderByCreatedAtDesc().stream()
                .map(OrderResponse::fromSimple)
                .collect(Collectors.toList());
    }

    /**
     * Get order by ID with items.
     */
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return OrderResponse.from(order);
    }

    /**
     * Get orders by table ID.
     */
    public List<OrderResponse> getOrdersByTable(Long tableId) {
        return orderRepository.findByTableIdAndIsClosedFalseOrderByCreatedAtDesc(tableId).stream()
                .map(OrderResponse::fromSimple)
                .collect(Collectors.toList());
    }

    /**
     * Create a new order.
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, Long userId) {
        log.info("[ORDER_CREATE] tableId={}, userId={}", request.getTableId(), userId);

        DiningTable table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("DiningTable", request.getTableId()));

        if (table.getStatus() == TableStatus.DISABLED) {
            throw new BusinessException("Cannot create order for disabled table");
        }

        // Generate order number: ORD + YYMMDD + sequence
        String orderNumber = generateOrderNumber();

        User user = userRepository.findById(userId).orElse(null);

        Order order = Order.builder()
                .table(table)
                .orderNumber(orderNumber)
                .status(OrderStatus.PENDING_CONFIRM)
                .note(request.getNote())
                .createdBy(user)
                .updatedBy(user)
                .build();

        // Update table status to OCCUPIED
        table.setStatus(TableStatus.OCCUPIED);
        tableRepository.save(table);

        Order saved = orderRepository.save(order);
        log.info("[ORDER_CREATED] id={}, orderNumber={}", saved.getId(), saved.getOrderNumber());

        return OrderResponse.from(saved);
    }

    /**
     * Add item to order.
     */
    @Transactional
    public OrderResponse addItem(Long orderId, AddOrderItemRequest request, Long userId) {
        log.info("[ORDER_ADD_ITEM] orderId={}, menuItemId={}, qty={}", orderId, request.getMenuItemId(),
                request.getQuantity());

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (order.getIsClosed()) {
            throw new BusinessException("Cannot add item to closed order");
        }

        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", request.getMenuItemId()));

        // Calculate options price and build options detail
        BigDecimal optionsPrice = BigDecimal.ZERO;
        String optionsDetail = null;

        if (request.getOptionIds() != null && !request.getOptionIds().isEmpty()) {
            List<Option> options = optionRepository.findAllById(request.getOptionIds());
            optionsPrice = options.stream()
                    .map(Option::getPriceDelta)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Create options detail JSON
            List<Map<String, Object>> optionsList = options.stream()
                    .map(opt -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", opt.getId());
                        map.put("name", opt.getName());
                        map.put("price", opt.getPriceDelta());
                        return map;
                    })
                    .collect(Collectors.toList());

            try {
                optionsDetail = objectMapper.writeValueAsString(optionsList);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize options", e);
            }
        }

        OrderItem item = OrderItem.builder()
                .order(order)
                .menuItem(menuItem)
                .menuItemName(menuItem.getName())
                .quantity(request.getQuantity() != null ? request.getQuantity() : 1)
                .unitPrice(menuItem.getBasePrice())
                .optionsDetail(optionsDetail)
                .optionsPrice(optionsPrice)
                .status(OrderItemStatus.PENDING)
                .note(request.getNote())
                .build();

        item.calculateLineTotal();
        order.addItem(item);

        // Update updatedBy
        User user = userRepository.findById(userId).orElse(null);
        order.setUpdatedBy(user);

        try {
            Order saved = orderRepository.save(order);
            log.info("[ORDER_ITEM_ADDED] orderId={}, itemId={}", saved.getId(), item.getId());
            return OrderResponse.from(saved);
        } catch (OptimisticLockingFailureException e) {
            throw new ConflictException("Order", orderId, order.getVersion(), null);
        }
    }

    /**
     * Update order status (for CASHIER).
     */
    @Transactional
    public OrderResponse updateStatus(Long orderId, String newStatus, Long userId) {
        log.info("[ORDER_STATUS_UPDATE] orderId={}, newStatus={}", orderId, newStatus);

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        OrderStatus status = OrderStatus.valueOf(newStatus.toUpperCase());
        order.setStatus(status);

        User user = userRepository.findById(userId).orElse(null);
        order.setUpdatedBy(user);

        // If cancelled, mark as closed and release table
        if (status == OrderStatus.CANCELLED) {
            order.setIsClosed(true);
            releaseTableIfNoOpenOrders(order.getTable().getId());
        }

        try {
            Order saved = orderRepository.save(order);
            log.info("[ORDER_STATUS_UPDATED] orderId={}, status={}", saved.getId(), saved.getStatus());
            return OrderResponse.from(saved);
        } catch (OptimisticLockingFailureException e) {
            throw new ConflictException("Order", orderId, order.getVersion(), null);
        }
    }

    private String generateOrderNumber() {
        String prefix = "ORD" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Integer maxNum = orderRepository.findMaxOrderNumberByPrefix(prefix);
        int nextNum = (maxNum != null ? maxNum : 0) + 1;
        return prefix + String.format("%04d", nextNum);
    }

    private void releaseTableIfNoOpenOrders(Long tableId) {
        boolean hasOpenOrders = orderRepository.existsByTableIdAndIsClosedFalse(tableId);
        if (!hasOpenOrders) {
            DiningTable table = tableRepository.findById(tableId).orElse(null);
            if (table != null && table.getStatus() == TableStatus.OCCUPIED) {
                table.setStatus(TableStatus.AVAILABLE);
                tableRepository.save(table);
                log.info("[TABLE_RELEASED] tableId={}", tableId);
            }
        }
    }
}
