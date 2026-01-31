package com.restaurant.backend.common.enums;

/**
 * Order status in the order lifecycle.
 */
public enum OrderStatus {
    /** Staff đã tạo, chờ cashier confirm */
    PENDING_CONFIRM,

    /** Cashier đã confirm, đang nấu */
    COOKING,

    /** Bếp xong */
    DONE,

    /** Đã phục vụ */
    SERVED,

    /** Đã hủy */
    CANCELLED
}
