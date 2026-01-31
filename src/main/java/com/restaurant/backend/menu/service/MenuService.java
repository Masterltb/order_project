package com.restaurant.backend.menu.service;

import com.restaurant.backend.common.enums.MenuItemStatus;
import com.restaurant.backend.common.exception.ResourceNotFoundException;
import com.restaurant.backend.menu.dto.CreateMenuItemRequest;
import com.restaurant.backend.menu.dto.MenuItemResponse;
import com.restaurant.backend.menu.dto.UpdateMenuItemRequest;
import com.restaurant.backend.menu.entity.MenuItem;
import com.restaurant.backend.menu.entity.OptionGroup;
import com.restaurant.backend.menu.repository.MenuItemRepository;
import com.restaurant.backend.menu.repository.OptionGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for MenuItem management.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final OptionGroupRepository optionGroupRepository;

    /**
     * Get all available menu items for public access.
     */
    public List<MenuItemResponse> getAvailableMenuItems() {
        return menuItemRepository.findByStatusOrderByCategoryAscNameAsc(MenuItemStatus.AVAILABLE).stream()
                .map(MenuItemResponse::fromSimple)
                .collect(Collectors.toList());
    }

    /**
     * Get menu items by category.
     */
    public List<MenuItemResponse> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategoryAndStatusOrderByNameAsc(category, MenuItemStatus.AVAILABLE).stream()
                .map(MenuItemResponse::fromSimple)
                .collect(Collectors.toList());
    }

    /**
     * Get all menu items for admin (including unavailable).
     */
    public List<MenuItemResponse> getAllMenuItems() {
        return menuItemRepository.findAllByOrderByCategoryAscNameAsc().stream()
                .map(MenuItemResponse::fromSimple)
                .collect(Collectors.toList());
    }

    /**
     * Get menu item by ID with option groups.
     */
    public MenuItemResponse getMenuItemById(Long id) {
        MenuItem item = menuItemRepository.findByIdWithOptionGroups(id);
        if (item == null) {
            throw new ResourceNotFoundException("MenuItem", id);
        }
        return MenuItemResponse.from(item);
    }

    /**
     * Get all categories.
     */
    public List<String> getCategories() {
        return menuItemRepository.findDistinctCategories();
    }

    /**
     * Create a new menu item.
     */
    @Transactional
    public MenuItemResponse createMenuItem(CreateMenuItemRequest request) {
        log.info("[MENU_CREATE] name={}, category={}", request.getName(), request.getCategory());

        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .availableFrom(request.getAvailableFrom())
                .availableTo(request.getAvailableTo())
                .status(MenuItemStatus.AVAILABLE)
                .build();

        if (request.getOptionGroupIds() != null && !request.getOptionGroupIds().isEmpty()) {
            Set<OptionGroup> groups = new HashSet<>(optionGroupRepository.findAllById(request.getOptionGroupIds()));
            item.setOptionGroups(groups);
        }

        MenuItem saved = menuItemRepository.save(item);
        log.info("[MENU_CREATED] id={}", saved.getId());

        return MenuItemResponse.from(saved);
    }

    /**
     * Update an existing menu item.
     */
    @Transactional
    public MenuItemResponse updateMenuItem(Long id, UpdateMenuItemRequest request) {
        log.info("[MENU_UPDATE] id={}", id);

        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", id));

        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getBasePrice() != null) {
            item.setBasePrice(request.getBasePrice());
        }
        if (request.getCategory() != null) {
            item.setCategory(request.getCategory());
        }
        if (request.getImageUrl() != null) {
            item.setImageUrl(request.getImageUrl());
        }
        if (request.getStatus() != null) {
            item.setStatus(MenuItemStatus.valueOf(request.getStatus().toUpperCase()));
        }
        if (request.getAvailableFrom() != null) {
            item.setAvailableFrom(request.getAvailableFrom());
        }
        if (request.getAvailableTo() != null) {
            item.setAvailableTo(request.getAvailableTo());
        }
        if (request.getOptionGroupIds() != null) {
            Set<OptionGroup> groups = new HashSet<>(optionGroupRepository.findAllById(request.getOptionGroupIds()));
            item.setOptionGroups(groups);
        }

        MenuItem saved = menuItemRepository.save(item);
        log.info("[MENU_UPDATED] id={}", saved.getId());

        return MenuItemResponse.from(saved);
    }
}
