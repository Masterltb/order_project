package com.restaurant.backend.menu.dto;

import com.restaurant.backend.common.enums.SelectionType;
import com.restaurant.backend.menu.entity.OptionGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Response DTO for OptionGroup with its options.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionGroupResponse {

    private Long id;
    private String name;
    private SelectionType selectionType;
    private Integer minSelect;
    private Integer maxSelect;
    private List<OptionResponse> options;

    public static OptionGroupResponse from(OptionGroup group) {
        return OptionGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .selectionType(group.getSelectionType())
                .minSelect(group.getMinSelect())
                .maxSelect(group.getMaxSelect())
                .options(group.getOptions().stream()
                        .filter(o -> "ACTIVE".equals(o.getStatus()))
                        .map(OptionResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
