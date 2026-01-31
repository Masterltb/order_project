package com.restaurant.backend.common.enums;

/**
 * Order item status in the order item lifecycle.
 */
public enum OrderItemStatus {
    /** Chờ xác nhận */
    PENDING,

    /** Đang nấu */
    COOKING,

    /** Xong */
    DONE,

    /** Đã phục vụ */
    SERVED,

    /** Hủy */
    CANCELLED
}
