package com.restaurant.backend.menu.entity;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.common.enums.SelectionType;
import jakarta.persistence.CascadeType;
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
 * OptionGroup entity representing option groups (Size, Toppings, etc.)
 */
@Entity
@Table(name = "option_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionGroup extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "selection_type", length = 20, nullable = false)
    @Builder.Default
    private SelectionType selectionType = SelectionType.SINGLE;

    @Column(name = "min_select", nullable = false)
    @Builder.Default
    private Integer minSelect = 0;

    @Column(name = "max_select", nullable = false)
    @Builder.Default
    private Integer maxSelect = 1;

    @Column(length = 20, nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Option> options = new ArrayList<>();
}
