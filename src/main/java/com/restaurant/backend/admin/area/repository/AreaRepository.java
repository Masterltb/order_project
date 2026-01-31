package com.restaurant.backend.admin.area.repository;

import com.restaurant.backend.admin.area.entity.Area;
import com.restaurant.backend.common.enums.AreaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Area entity.
 */
@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    Optional<Area> findByCode(String code);

    boolean existsByCode(String code);

    List<Area> findByStatusOrderBySortOrderAsc(AreaStatus status);

    List<Area> findAllByOrderBySortOrderAsc();
}
