package com.restaurant.backend.admin.area.controller;

import com.restaurant.backend.admin.area.dto.AreaResponse;
import com.restaurant.backend.admin.area.dto.CreateAreaRequest;
import com.restaurant.backend.admin.area.dto.UpdateAreaRequest;
import com.restaurant.backend.admin.area.service.AreaService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin controller for Area management.
 */
@RestController
@RequestMapping("/admin/areas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AreaController {

    private final AreaService areaService;

    @GetMapping
    public ApiResponse<List<AreaResponse>> getAllAreas() {
        return ApiResponse.success(areaService.getAllAreas());
    }

    @GetMapping("/{id}")
    public ApiResponse<AreaResponse> getAreaById(@PathVariable Long id) {
        return ApiResponse.success(areaService.getAreaById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AreaResponse> createArea(@Valid @RequestBody CreateAreaRequest request) {
        return ApiResponse.success(areaService.createArea(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<AreaResponse> updateArea(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAreaRequest request) {
        return ApiResponse.success(areaService.updateArea(id, request));
    }
}
