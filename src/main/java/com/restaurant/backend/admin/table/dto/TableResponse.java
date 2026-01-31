package com.restaurant.backend.admin.table.dto;

import com.restaurant.backend.admin.table.entity.DiningTable;
import com.restaurant.backend.common.enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for DiningTable.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableResponse {

    private Long id;
    private Long areaId;
    private String areaCode;
    private String areaName;
    private String tableCode;
    private Integer capacity;
    private TableStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public static TableResponse from(DiningTable table) {
        return TableResponse.builder()
                .id(table.getId())
                .areaId(table.getArea().getId())
                .areaCode(table.getArea().getCode())
                .areaName(table.getArea().getName())
                .tableCode(table.getTableCode())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .createdAt(table.getCreatedAt())
                .updatedAt(table.getUpdatedAt())
                .build();
    }
}
