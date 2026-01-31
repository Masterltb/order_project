package com.restaurant.backend.kitchen.controller;

import com.restaurant.backend.common.dto.ApiResponse;
import com.restaurant.backend.kitchen.dto.KitchenOrderItemResponse;
import com.restaurant.backend.kitchen.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Kitchen Controller for kitchen display and item status updates.
 */
@RestController
@RequestMapping("/kitchen")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF', 'CASHIER', 'ADMIN')")
public class KitchenController {

    private final KitchenService kitchenService;

    /**
     * Get pending items for kitchen display.
     */
    @GetMapping("/items")
    public ApiResponse<List<KitchenOrderItemResponse>> getPendingItems() {
        return ApiResponse.success(kitchenService.getPendingItems());
    }

    /**
     * Start cooking an item.
     */
    @PatchMapping("/items/{id}/start")
    public ApiResponse<KitchenOrderItemResponse> startCooking(@PathVariable Long id) {
        return ApiResponse.success(kitchenService.startCooking(id));
    }

    /**
     * Mark item as done.
     */
    @PatchMapping("/items/{id}/done")
    public ApiResponse<KitchenOrderItemResponse> markDone(@PathVariable Long id) {
        return ApiResponse.success(kitchenService.markDone(id));
    }

    /**
     * Mark item as served.
     */
    @PatchMapping("/items/{id}/served")
    public ApiResponse<KitchenOrderItemResponse> markServed(@PathVariable Long id) {
        return ApiResponse.success(kitchenService.markServed(id));
    }
}
