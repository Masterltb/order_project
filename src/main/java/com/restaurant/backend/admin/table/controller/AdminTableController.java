package com.restaurant.backend.admin.table.controller;

import com.restaurant.backend.admin.table.dto.CreateTableRequest;
import com.restaurant.backend.admin.table.dto.TableResponse;
import com.restaurant.backend.admin.table.dto.UpdateTableRequest;
import com.restaurant.backend.admin.table.service.TableService;
import com.restaurant.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * Admin controller for DiningTable management.
 */
@RestController
@RequestMapping("/admin/tables")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTableController {

    private final TableService tableService;

    @GetMapping
    public ApiResponse<List<TableResponse>> getTablesByArea(@RequestParam(required = false) Long areaId) {
        if (areaId != null) {
            return ApiResponse.success(tableService.getTablesByArea(areaId));
        }
        return ApiResponse.success(tableService.getActiveTables());
    }

    @GetMapping("/{id}")
    public ApiResponse<TableResponse> getTableById(@PathVariable Long id) {
        return ApiResponse.success(tableService.getTableById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TableResponse> createTable(@Valid @RequestBody CreateTableRequest request) {
        return ApiResponse.success(tableService.createTable(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<TableResponse> updateTable(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTableRequest request) {
        return ApiResponse.success(tableService.updateTable(id, request));
    }

    @PatchMapping("/{id}/disable")
    public ApiResponse<TableResponse> disableTable(@PathVariable Long id) {
        return ApiResponse.success(tableService.disableTable(id));
    }

    @PatchMapping("/{id}/enable")
    public ApiResponse<TableResponse> enableTable(@PathVariable Long id) {
        return ApiResponse.success(tableService.enableTable(id));
    }
}
