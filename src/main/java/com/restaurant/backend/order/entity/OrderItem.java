package com.restaurant.backend.order.entity;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.common.enums.OrderItemStatus;
import com.restaurant.backend.menu.entity.MenuItem;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

/**
 * OrderItem entity with snapshot pricing and options.
 */
@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "menu_item_name", nullable = false, length = 200)
    private String menuItemName;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "options_detail", columnDefinition = "jsonb")
    private String optionsDetail;

    @Column(name = "options_price", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal optionsPrice = BigDecimal.ZERO;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private OrderItemStatus status = OrderItemStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String note;

    /**
     * Calculate and set line total.
     */
    public void calculateLineTotal() {
        BigDecimal pricePerUnit = this.unitPrice.add(this.optionsPrice != null ? this.optionsPrice : BigDecimal.ZERO);
        this.lineTotal = pricePerUnit.multiply(BigDecimal.valueOf(this.quantity));
    }
}
