package com.restaurant.backend.admin.table.service;

import com.restaurant.backend.admin.area.entity.Area;
import com.restaurant.backend.admin.area.repository.AreaRepository;
import com.restaurant.backend.admin.table.dto.CreateTableRequest;
import com.restaurant.backend.admin.table.dto.TableResponse;
import com.restaurant.backend.admin.table.dto.UpdateTableRequest;
import com.restaurant.backend.admin.table.entity.DiningTable;
import com.restaurant.backend.admin.table.repository.DiningTableRepository;
import com.restaurant.backend.common.enums.TableStatus;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for DiningTable management.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TableService {

    private final DiningTableRepository tableRepository;
    private final AreaRepository areaRepository;

    /**
     * Get all tables by area.
     */
    public List<TableResponse> getTablesByArea(Long areaId) {
        return tableRepository.findByAreaIdOrderByTableCodeAsc(areaId).stream()
                .map(TableResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Get all active tables (not DISABLED) for staff/cashier.
     */
    public List<TableResponse> getActiveTables() {
        return tableRepository.findAllActiveOrderByAreaAndCode(TableStatus.DISABLED).stream()
                .map(TableResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Get table by ID.
     */
    public TableResponse getTableById(Long id) {
        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DiningTable", id));
        return TableResponse.from(table);
    }

    /**
     * Create a new table.
     */
    @Transactional
    public TableResponse createTable(CreateTableRequest request) {
        log.info("[TABLE_CREATE] areaId={}, tableCode={}", request.getAreaId(), request.getTableCode());

        Area area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Area", request.getAreaId()));

        if (tableRepository.existsByAreaIdAndTableCode(request.getAreaId(), request.getTableCode())) {
            throw new BusinessException("Table code already exists in this area: " + request.getTableCode());
        }

        DiningTable table = DiningTable.builder()
                .area(area)
                .tableCode(request.getTableCode().toUpperCase())
                .capacity(request.getCapacity() != null ? request.getCapacity() : 4)
                .status(TableStatus.AVAILABLE)
                .build();

        DiningTable saved = tableRepository.save(table);
        log.info("[TABLE_CREATED] id={}, tableCode={}", saved.getId(), saved.getTableCode());

        return TableResponse.from(saved);
    }

    /**
     * Update an existing table.
     */
    @Transactional
    public TableResponse updateTable(Long id, UpdateTableRequest request) {
        log.info("[TABLE_UPDATE] id={}", id);

        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DiningTable", id));

        if (request.getAreaId() != null && !request.getAreaId().equals(table.getArea().getId())) {
            Area newArea = areaRepository.findById(request.getAreaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Area", request.getAreaId()));
            table.setArea(newArea);
        }
        if (request.getCapacity() != null) {
            table.setCapacity(request.getCapacity());
        }

        DiningTable saved = tableRepository.save(table);
        log.info("[TABLE_UPDATED] id={}", saved.getId());

        return TableResponse.from(saved);
    }

    /**
     * Disable a table (only if no open order).
     */
    @Transactional
    public TableResponse disableTable(Long id) {
        log.info("[TABLE_DISABLE] id={}", id);

        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DiningTable", id));

        // TODO: Check for open orders when Order entity is implemented
        // if (orderRepository.existsByTableIdAndIsClosedFalse(id)) {
        // throw new BusinessException("Cannot disable table with open order");
        // }

        if (table.getStatus() == TableStatus.OCCUPIED) {
            throw new BusinessException("Cannot disable table that is currently occupied");
        }

        table.setStatus(TableStatus.DISABLED);
        DiningTable saved = tableRepository.save(table);
        log.info("[TABLE_DISABLED] id={}", saved.getId());

        return TableResponse.from(saved);
    }

    /**
     * Enable a table.
     */
    @Transactional
    public TableResponse enableTable(Long id) {
        log.info("[TABLE_ENABLE] id={}", id);

        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DiningTable", id));

        table.setStatus(TableStatus.AVAILABLE);
        DiningTable saved = tableRepository.save(table);
        log.info("[TABLE_ENABLED] id={}", saved.getId());

        return TableResponse.from(saved);
    }
}
