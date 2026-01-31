package com.restaurant.backend.admin.area.dto;

import com.restaurant.backend.admin.area.entity.Area;
import com.restaurant.backend.common.enums.AreaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Area.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaResponse {

    private Long id;
    private String code;
    private String name;
    private Integer sortOrder;
    private AreaStatus status;
    private Integer tableCount;
    private Instant createdAt;
    private Instant updatedAt;

    public static AreaResponse from(Area area) {
        return AreaResponse.builder()
                .id(area.getId())
                .code(area.getCode())
                .name(area.getName())
                .sortOrder(area.getSortOrder())
                .status(area.getStatus())
                .tableCount(area.getTables() != null ? area.getTables().size() : 0)
                .createdAt(area.getCreatedAt())
                .updatedAt(area.getUpdatedAt())
                .build();
    }
}
