package com.restaurant.backend.menu.entity;

import com.restaurant.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Option entity representing individual options within an OptionGroup.
 */
@Entity
@Table(name = "option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_group_id", nullable = false)
    private OptionGroup optionGroup;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "price_delta", precision = 12, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal priceDelta = BigDecimal.ZERO;

    @Column(length = 20, nullable = false)
    @Builder.Default
    private String status = "ACTIVE";
}
