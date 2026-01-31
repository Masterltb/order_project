package com.restaurant.backend.admin.table.repository;

import com.restaurant.backend.admin.table.entity.DiningTable;
import com.restaurant.backend.common.enums.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for DiningTable entity.
 */
@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {

    List<DiningTable> findByAreaIdOrderByTableCodeAsc(Long areaId);

    List<DiningTable> findByAreaIdAndStatusNotOrderByTableCodeAsc(Long areaId, TableStatus excludeStatus);

    @Query("SELECT t FROM DiningTable t WHERE t.status != :excludeStatus ORDER BY t.area.sortOrder, t.tableCode")
    List<DiningTable> findAllActiveOrderByAreaAndCode(@Param("excludeStatus") TableStatus excludeStatus);

    boolean existsByAreaIdAndTableCode(Long areaId, String tableCode);

    Optional<DiningTable> findByAreaIdAndTableCode(Long areaId, String tableCode);
}
