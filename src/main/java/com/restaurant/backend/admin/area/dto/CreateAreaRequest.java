package com.restaurant.backend.admin.area.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating an Area.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAreaRequest {

    @NotBlank(message = "Code is required")
    @Size(max = 10, message = "Code must be at most 10 characters")
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    private Integer sortOrder = 0;
}
