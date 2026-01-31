package com.restaurant.backend.payment.service;

import com.restaurant.backend.admin.table.entity.DiningTable;
import com.restaurant.backend.admin.table.repository.DiningTableRepository;
import com.restaurant.backend.admin.user.entity.User;
import com.restaurant.backend.admin.user.repository.UserRepository;
import com.restaurant.backend.common.enums.OrderStatus;
import com.restaurant.backend.common.enums.PaymentMethod;
import com.restaurant.backend.common.enums.PaymentStatus;
import com.restaurant.backend.common.enums.TableStatus;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ResourceNotFoundException;
import com.restaurant.backend.order.entity.Order;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.payment.dto.PaymentRequest;
import com.restaurant.backend.payment.dto.PaymentResponse;
import com.restaurant.backend.payment.entity.Payment;
import com.restaurant.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for Payment processing.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final DiningTableRepository tableRepository;
    private final UserRepository userRepository;

    /**
     * Process payment for an order.
     */
    @Transactional
    public PaymentResponse processPayment(Long orderId, PaymentRequest request, Long userId) {
        log.info("[PAYMENT_PROCESS] orderId={}, method={}", orderId, request.getPaymentMethod());

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (order.getIsClosed()) {
            throw new BusinessException("Order is already closed");
        }

        // Check if payment already exists
        if (paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.SUCCEEDED)) {
            throw new BusinessException("Order already paid");
        }

        PaymentMethod method = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        User user = userRepository.findById(userId).orElse(null);

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(method)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .createdBy(user)
                .build();

        // For CASH, immediately succeed
        if (method == PaymentMethod.CASH) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            payment.setPaidAt(Instant.now());
            payment.setTransactionRef("CASH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            // Close order and release table
            order.setIsClosed(true);
            order.setStatus(OrderStatus.SERVED);
            orderRepository.save(order);

            releaseTableIfNoOpenOrders(order.getTable().getId());
        } else {
            // For MOMO/ZALOPAY, generate pending transaction ref (will be updated by
            // callback)
            payment.setTransactionRef(method.name() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        Payment saved = paymentRepository.save(payment);
        log.info("[PAYMENT_CREATED] id={}, status={}", saved.getId(), saved.getStatus());

        return PaymentResponse.from(saved);
    }

    /**
     * Confirm e-wallet payment (webhook callback).
     */
    @Transactional
    public PaymentResponse confirmPayment(Long paymentId, boolean success) {
        log.info("[PAYMENT_CONFIRM] paymentId={}, success={}", paymentId, success);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Payment is not pending");
        }

        if (success) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            payment.setPaidAt(Instant.now());

            // Close order and release table
            Order order = payment.getOrder();
            order.setIsClosed(true);
            order.setStatus(OrderStatus.SERVED);
            orderRepository.save(order);

            releaseTableIfNoOpenOrders(order.getTable().getId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        Payment saved = paymentRepository.save(payment);
        log.info("[PAYMENT_CONFIRMED] id={}, status={}", saved.getId(), saved.getStatus());

        return PaymentResponse.from(saved);
    }

    /**
     * Get payments for an order.
     */
    public List<PaymentResponse> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId).stream()
                .map(PaymentResponse::from)
                .collect(Collectors.toList());
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
