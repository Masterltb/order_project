package com.restaurant.backend.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for adding an item to an Order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrderItemRequest {

    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    private List<Long> optionIds;

    private String note;
}
