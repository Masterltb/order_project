package com.restaurant.backend.common.enums;

/**
 * Dining table status.
 */
public enum TableStatus {
    /** Trống, sẵn sàng phục vụ */
    AVAILABLE,

    /** Có khách (có order open) */
    OCCUPIED,

    /** Tạm khóa (admin) */
    DISABLED
}
