package com.restaurant.backend.admin.table.entity;

import com.restaurant.backend.admin.area.entity.Area;
import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.common.enums.TableStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DiningTable entity representing restaurant tables.
 */
@Entity
@Table(name = "dining_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiningTable extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @Column(name = "table_code", nullable = false, length = 10)
    private String tableCode;

    @Builder.Default
    private Integer capacity = 4;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private TableStatus status = TableStatus.AVAILABLE;
}
