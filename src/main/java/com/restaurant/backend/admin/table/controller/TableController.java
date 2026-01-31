package com.restaurant.backend.admin.table.controller;

import com.restaurant.backend.admin.table.dto.TableResponse;
import com.restaurant.backend.admin.table.service.TableService;
import com.restaurant.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public controller for Tables (STAFF, CASHIER).
 * Returns only active tables (not DISABLED).
 */
@RestController
@RequestMapping("/tables")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF', 'CASHIER')")
public class TableController {

    private final TableService tableService;

    /**
     * Get all active tables for Staff/Cashier.
     * DISABLED tables are not returned.
     */
    @GetMapping
    public ApiResponse<List<TableResponse>> getActiveTables() {
        return ApiResponse.success(tableService.getActiveTables());
    }

    /**
     * Get a specific table by ID.
     */
    @GetMapping("/{id}")
    public ApiResponse<TableResponse> getTableById(@PathVariable Long id) {
        return ApiResponse.success(tableService.getTableById(id));
    }
}
