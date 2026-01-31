package com.restaurant.backend.menu.controller;

import com.restaurant.backend.common.dto.ApiResponse;
import com.restaurant.backend.menu.dto.MenuItemResponse;
import com.restaurant.backend.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public controller for Menu (STAFF, CASHIER).
 * Returns only available menu items.
 */
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * Get all available menu items.
     */
    @GetMapping
    public ApiResponse<List<MenuItemResponse>> getAvailableMenuItems(
            @RequestParam(required = false) String category) {
        if (category != null) {
            return ApiResponse.success(menuService.getMenuItemsByCategory(category));
        }
        return ApiResponse.success(menuService.getAvailableMenuItems());
    }

    /**
     * Get menu item by ID with option groups.
     */
    @GetMapping("/{id}")
    public ApiResponse<MenuItemResponse> getMenuItemById(@PathVariable Long id) {
        return ApiResponse.success(menuService.getMenuItemById(id));
    }

    /**
     * Get all categories.
     */
    @GetMapping("/categories")
    public ApiResponse<List<String>> getCategories() {
        return ApiResponse.success(menuService.getCategories());
    }
}
