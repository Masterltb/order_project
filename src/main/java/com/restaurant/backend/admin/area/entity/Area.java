package com.restaurant.backend.admin.area.entity;

import com.restaurant.backend.admin.table.entity.DiningTable;
import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.common.enums.AreaStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Area entity representing restaurant zones (A, B, C, VIP).
 */
@Entity
@Table(name = "area")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Area extends BaseEntity {

    @Column(nullable = false, length = 10, unique = true)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private AreaStatus status = AreaStatus.ACTIVE;

    @OneToMany(mappedBy = "area")
    @Builder.Default
    private List<DiningTable> tables = new ArrayList<>();
}
