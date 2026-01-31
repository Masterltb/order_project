package com.restaurant.backend.menu.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * Request DTO for updating a MenuItem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuItemRequest {

    @Size(max = 200, message = "Name must be at most 200 characters")
    private String name;

    private String description;

    @DecimalMin(value = "0", message = "Base price must be >= 0")
    private BigDecimal basePrice;

    @Size(max = 50, message = "Category must be at most 50 characters")
    private String category;

    private String imageUrl;

    private String status; // AVAILABLE or UNAVAILABLE

    private LocalTime availableFrom;

    private LocalTime availableTo;

    private List<Long> optionGroupIds;
}
