package com.restaurant.backend.payment.controller;

import com.restaurant.backend.common.dto.ApiResponse;
import com.restaurant.backend.payment.dto.PaymentResponse;
import com.restaurant.backend.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Payment callback controller for e-wallet webhooks.
 * This endpoint is public (configured in SecurityConfig).
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentCallbackController {

    private final PaymentService paymentService;

    /**
     * Callback endpoint for e-wallet payment confirmation.
     * Called by MOMO/ZaloPay when payment is completed.
     */
    @PostMapping("/{id}/callback")
    public ApiResponse<PaymentResponse> paymentCallback(
            @PathVariable Long id,
            @RequestParam boolean success) {
        log.info("[PAYMENT_CALLBACK] paymentId={}, success={}", id, success);
        return ApiResponse.success(paymentService.confirmPayment(id, success));
    }
}
