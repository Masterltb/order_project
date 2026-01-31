package com.restaurant.backend.menu.dto;

import com.restaurant.backend.common.enums.MenuItemStatus;
import com.restaurant.backend.menu.entity.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Response DTO for MenuItem with option groups.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String category;
    private String imageUrl;
    private MenuItemStatus status;
    private LocalTime availableFrom;
    private LocalTime availableTo;
    private List<OptionGroupResponse> optionGroups;

    public static MenuItemResponse from(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .basePrice(item.getBasePrice())
                .category(item.getCategory())
                .imageUrl(item.getImageUrl())
                .status(item.getStatus())
                .availableFrom(item.getAvailableFrom())
                .availableTo(item.getAvailableTo())
                .optionGroups(item.getOptionGroups() != null ? item.getOptionGroups().stream()
                        .filter(og -> "ACTIVE".equals(og.getStatus()))
                        .map(OptionGroupResponse::from)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public static MenuItemResponse fromSimple(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .basePrice(item.getBasePrice())
                .category(item.getCategory())
                .imageUrl(item.getImageUrl())
                .status(item.getStatus())
                .availableFrom(item.getAvailableFrom())
                .availableTo(item.getAvailableTo())
                .build();
    }
}
