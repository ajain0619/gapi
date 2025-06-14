package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.GeoSegment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GeoSegmentRepository {
  /** Search for all {@link GeoSegment} containing requested name of a given type. */
  Page<GeoSegment> findByNameContainingIgnoreCaseAndType(String name, Long type, Pageable pageable);

  /** Search for all {@link GeoSegment} containing requested name */
  Page<GeoSegment> findByNameContainingIgnoreCase(String name, Pageable pageable);

  /** Checks if a country exists with a given woeId and name. */
  boolean existsCountryByWoeIdAndName(Long woeId, String name);
}
