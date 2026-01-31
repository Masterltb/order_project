package com.restaurant.backend.menu.controller;

import com.restaurant.backend.common.dto.ApiResponse;
import com.restaurant.backend.menu.dto.CreateMenuItemRequest;
import com.restaurant.backend.menu.dto.MenuItemResponse;
import com.restaurant.backend.menu.dto.UpdateMenuItemRequest;
import com.restaurant.backend.menu.service.MenuService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin controller for Menu management.
 */
@RestController
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMenuController {

    private final MenuService menuService;

    /**
     * Get all menu items (including unavailable).
     */
    @GetMapping
    public ApiResponse<List<MenuItemResponse>> getAllMenuItems() {
        return ApiResponse.success(menuService.getAllMenuItems());
    }

    /**
     * Get menu item by ID with option groups.
     */
    @GetMapping("/{id}")
    public ApiResponse<MenuItemResponse> getMenuItemById(@PathVariable Long id) {
        return ApiResponse.success(menuService.getMenuItemById(id));
    }

    /**
     * Create a new menu item.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MenuItemResponse> createMenuItem(@Valid @RequestBody CreateMenuItemRequest request) {
        return ApiResponse.success(menuService.createMenuItem(request));
    }

    /**
     * Update an existing menu item.
     */
    @PatchMapping("/{id}")
    public ApiResponse<MenuItemResponse> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuItemRequest request) {
        return ApiResponse.success(menuService.updateMenuItem(id, request));
    }
}
