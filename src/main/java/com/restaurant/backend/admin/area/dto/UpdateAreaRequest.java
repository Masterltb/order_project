package com.restaurant.backend.admin.area.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an Area.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAreaRequest {

    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    private Integer sortOrder;

    private String status; // ACTIVE or INACTIVE
}
