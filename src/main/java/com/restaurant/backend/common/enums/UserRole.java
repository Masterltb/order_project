package com.restaurant.backend.common.enums;

/**
 * User roles for RBAC.
 */
public enum UserRole {
    /** Quản trị viên - full quyền */
    ADMIN,

    /** Thu ngân - quản lý order, payment */
    CASHIER,

    /** Nhân viên phục vụ - tạo order, thêm món */
    STAFF
}
