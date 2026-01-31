package com.restaurant.backend.order.controller;

import com.restaurant.backend.auth.security.UserPrincipal;
import com.restaurant.backend.common.dto.ApiResponse;
import com.restaurant.backend.order.dto.AddOrderItemRequest;
import com.restaurant.backend.order.dto.CreateOrderRequest;
import com.restaurant.backend.order.dto.OrderResponse;
import com.restaurant.backend.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for Order management (STAFF operations).
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF', 'CASHIER')")
public class OrderController {

    private final OrderService orderService;

    /**
     * Get all open orders.
     */
    @GetMapping
    public ApiResponse<List<OrderResponse>> getOpenOrders(
            @RequestParam(required = false) Long tableId) {
        if (tableId != null) {
            return ApiResponse.success(orderService.getOrdersByTable(tableId));
        }
        return ApiResponse.success(orderService.getOpenOrders());
    }

    /**
     * Get order by ID with items.
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderById(id));
    }

    /**
     * Create a new order (STAFF).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STAFF')")
    public ApiResponse<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(orderService.createOrder(request, user.getId()));
    }

    /**
     * Add item to order (STAFF).
     */
    @PostMapping("/{id}/items")
    @PreAuthorize("hasRole('STAFF')")
    public ApiResponse<OrderResponse> addItem(
            @PathVariable Long id,
            @Valid @RequestBody AddOrderItemRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(orderService.addItem(id, request, user.getId()));
    }

    /**
     * Update order status (CASHIER only for confirm).
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('CASHIER')")
    public ApiResponse<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(orderService.updateStatus(id, status, user.getId()));
    }
}
