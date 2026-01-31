package com.restaurant.backend.menu.dto;

import com.restaurant.backend.common.enums.SelectionType;
import com.restaurant.backend.menu.entity.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for an individual Option.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionResponse {

    private Long id;
    private String name;
    private BigDecimal priceDelta;

    public static OptionResponse from(Option option) {
        return OptionResponse.builder()
                .id(option.getId())
                .name(option.getName())
                .priceDelta(option.getPriceDelta())
                .build();
    }
}
