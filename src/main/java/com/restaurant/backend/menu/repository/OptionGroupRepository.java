package com.restaurant.backend.menu.repository;

import com.restaurant.backend.menu.entity.OptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for OptionGroup entity.
 */
@Repository
public interface OptionGroupRepository extends JpaRepository<OptionGroup, Long> {

    List<OptionGroup> findByStatusOrderByNameAsc(String status);

    @Query("SELECT og FROM OptionGroup og LEFT JOIN FETCH og.options WHERE og.id = :id")
    OptionGroup findByIdWithOptions(@Param("id") Long id);
}
