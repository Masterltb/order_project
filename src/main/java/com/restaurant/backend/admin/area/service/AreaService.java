package com.restaurant.backend.admin.area.service;

import com.restaurant.backend.admin.area.dto.AreaResponse;
import com.restaurant.backend.admin.area.dto.CreateAreaRequest;
import com.restaurant.backend.admin.area.dto.UpdateAreaRequest;
import com.restaurant.backend.admin.area.entity.Area;
import com.restaurant.backend.admin.area.repository.AreaRepository;
import com.restaurant.backend.common.enums.AreaStatus;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Area management.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AreaService {

    private final AreaRepository areaRepository;

    /**
     * Get all areas ordered by sortOrder.
     */
    public List<AreaResponse> getAllAreas() {
        return areaRepository.findAllByOrderBySortOrderAsc().stream()
                .map(AreaResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Get area by ID.
     */
    public AreaResponse getAreaById(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area", id));
        return AreaResponse.from(area);
    }

    /**
     * Create a new area.
     */
    @Transactional
    public AreaResponse createArea(CreateAreaRequest request) {
        log.info("[AREA_CREATE] code={}, name={}", request.getCode(), request.getName());

        if (areaRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Area code already exists: " + request.getCode());
        }

        Area area = Area.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .status(AreaStatus.ACTIVE)
                .build();

        Area saved = areaRepository.save(area);
        log.info("[AREA_CREATED] id={}, code={}", saved.getId(), saved.getCode());

        return AreaResponse.from(saved);
    }

    /**
     * Update an existing area.
     */
    @Transactional
    public AreaResponse updateArea(Long id, UpdateAreaRequest request) {
        log.info("[AREA_UPDATE] id={}", id);

        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area", id));

        if (request.getName() != null) {
            area.setName(request.getName());
        }
        if (request.getSortOrder() != null) {
            area.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            area.setStatus(AreaStatus.valueOf(request.getStatus().toUpperCase()));
        }

        Area saved = areaRepository.save(area);
        log.info("[AREA_UPDATED] id={}", saved.getId());

        return AreaResponse.from(saved);
    }
}
