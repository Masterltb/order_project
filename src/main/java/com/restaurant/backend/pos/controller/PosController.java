package com.restaurant.backend.pos.controller;

import com.restaurant.backend.auth.security.UserPrincipal;
import com.restaurant.backend.common.dto.ApiResponse;
import com.restaurant.backend.order.dto.OrderResponse;
import com.restaurant.backend.order.service.OrderService;
import com.restaurant.backend.payment.dto.PaymentRequest;
import com.restaurant.backend.payment.dto.PaymentResponse;
import com.restaurant.backend.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * POS Controller for Cashier operations.
 */
@RestController
@RequestMapping("/pos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CASHIER')")
public class PosController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    /**
     * Get all open orders for POS.
     */
    @GetMapping("/orders")
    public ApiResponse<List<OrderResponse>> getOpenOrders() {
        return ApiResponse.success(orderService.getOpenOrders());
    }

    /**
     * Get order details for checkout.
     */
    @GetMapping("/orders/{id}")
    public ApiResponse<OrderResponse> getOrderForCheckout(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderById(id));
    }

    /**
     * Confirm order (change from PENDING_CONFIRM to COOKING).
     */
    @PatchMapping("/orders/{id}/confirm")
    public ApiResponse<OrderResponse> confirmOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(orderService.updateStatus(id, "COOKING", user.getId()));
    }

    /**
     * Process payment for order.
     */
    @PostMapping("/orders/{id}/pay")
    public ApiResponse<PaymentResponse> processPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(paymentService.processPayment(id, request, user.getId()));
    }

    /**
     * Cancel order.
     */
    @PatchMapping("/orders/{id}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(orderService.updateStatus(id, "CANCELLED", user.getId()));
    }

    /**
     * Get payment history for order.
     */
    @GetMapping("/orders/{id}/payments")
    public ApiResponse<List<PaymentResponse>> getPaymentsByOrder(@PathVariable Long id) {
        return ApiResponse.success(paymentService.getPaymentsByOrder(id));
    }
}
