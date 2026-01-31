package com.restaurant.backend.menu.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * Request DTO for creating a MenuItem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuItemRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must be at most 200 characters")
    private String name;

    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0", message = "Base price must be >= 0")
    private BigDecimal basePrice;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must be at most 50 characters")
    private String category;

    private String imageUrl;

    private LocalTime availableFrom;

    private LocalTime availableTo;

    private List<Long> optionGroupIds;
}
