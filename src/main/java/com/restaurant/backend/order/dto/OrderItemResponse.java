package com.restaurant.backend.order.dto;

import com.restaurant.backend.common.enums.OrderItemStatus;
import com.restaurant.backend.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response DTO for OrderItem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String optionsDetail;
    private BigDecimal optionsPrice;
    private BigDecimal lineTotal;
    private OrderItemStatus status;
    private String note;
    private Instant createdAt;

    public static OrderItemResponse from(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItem().getId())
                .menuItemName(item.getMenuItemName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .optionsDetail(item.getOptionsDetail())
                .optionsPrice(item.getOptionsPrice())
                .lineTotal(item.getLineTotal())
                .status(item.getStatus())
                .note(item.getNote())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
