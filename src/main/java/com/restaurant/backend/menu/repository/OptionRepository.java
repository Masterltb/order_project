package com.restaurant.backend.menu.repository;

import com.restaurant.backend.menu.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Option entity.
 */
@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByOptionGroupIdOrderByNameAsc(Long optionGroupId);
}
