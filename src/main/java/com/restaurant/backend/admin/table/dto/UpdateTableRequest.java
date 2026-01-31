package com.restaurant.backend.admin.table.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a DiningTable.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTableRequest {

    private Long areaId;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}
