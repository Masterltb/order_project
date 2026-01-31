package com.restaurant.backend.kitchen.dto;

import com.restaurant.backend.common.enums.OrderItemStatus;
import com.restaurant.backend.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Kitchen display.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitchenOrderItemResponse {

    private Long id;
    private Long orderId;
    private String orderNumber;
    private String tableCode;
    private String menuItemName;
    private Integer quantity;
    private String optionsDetail;
    private String note;
    private OrderItemStatus status;
    private Instant createdAt;

    public static KitchenOrderItemResponse from(OrderItem item) {
        return KitchenOrderItemResponse.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .orderNumber(item.getOrder().getOrderNumber())
                .tableCode(item.getOrder().getTable().getTableCode())
                .menuItemName(item.getMenuItemName())
                .quantity(item.getQuantity())
                .optionsDetail(item.getOptionsDetail())
                .note(item.getNote())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
