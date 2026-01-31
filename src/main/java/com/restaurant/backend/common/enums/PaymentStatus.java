package com.restaurant.backend.common.enums;

/**
 * Payment transaction status.
 */
public enum PaymentStatus {
    /** Đang chờ xử lý */
    PENDING,

    /** Thanh toán thành công */
    SUCCEEDED,

    /** Thanh toán thất bại */
    FAILED
}
