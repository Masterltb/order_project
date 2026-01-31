package com.restaurant.backend.admin.table.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a DiningTable.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTableRequest {

    @NotNull(message = "Area ID is required")
    private Long areaId;

    @NotBlank(message = "Table code is required")
    @Size(max = 10, message = "Table code must be at most 10 characters")
    private String tableCode;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity = 4;
}
