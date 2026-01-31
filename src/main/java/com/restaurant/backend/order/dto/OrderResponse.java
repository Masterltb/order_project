package com.restaurant.backend.order.dto;

import com.restaurant.backend.common.enums.OrderStatus;
import com.restaurant.backend.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Response DTO for Order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private Long tableId;
    private String tableCode;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String note;
    private Boolean isClosed;
    private Integer version;
    private List<OrderItemResponse> items;
    private Instant createdAt;
    private Instant updatedAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .tableId(order.getTable().getId())
                .tableCode(order.getTable().getTableCode())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .isClosed(order.getIsClosed())
                .version(order.getVersion())
                .items(order.getItems() != null ? order.getItems().stream()
                        .map(OrderItemResponse::from)
                        .collect(Collectors.toList()) : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public static OrderResponse fromSimple(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .tableId(order.getTable().getId())
                .tableCode(order.getTable().getTableCode())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .totalAmount(order.getTotalAmount())
                .isClosed(order.getIsClosed())
                .version(order.getVersion())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
