package com.restaurant.backend.menu.repository;

import com.restaurant.backend.common.enums.MenuItemStatus;
import com.restaurant.backend.menu.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for MenuItem entity.
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByStatusOrderByCategoryAscNameAsc(MenuItemStatus status);

    List<MenuItem> findByCategoryAndStatusOrderByNameAsc(String category, MenuItemStatus status);

    List<MenuItem> findAllByOrderByCategoryAscNameAsc();

    @Query("SELECT DISTINCT m.category FROM MenuItem m ORDER BY m.category")
    List<String> findDistinctCategories();

    @Query("SELECT m FROM MenuItem m LEFT JOIN FETCH m.optionGroups WHERE m.id = :id")
    MenuItem findByIdWithOptionGroups(@Param("id") Long id);
}
