package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.GeoSegment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaGeoSegmentRepository
    extends JpaRepository<GeoSegment, Long>, GeoSegmentRepository {

  /** {@inheritDoc} */
  Page<GeoSegment> findByNameContainingIgnoreCaseAndType(String name, Long type, Pageable pageable);

  /** {@inheritDoc} */
  Page<GeoSegment> findByNameContainingIgnoreCase(String name, Pageable pageable);

  /** {@inheritDoc} */
  @Query(
      "SELECT CASE WHEN count(gs) > 0 THEN true ELSE false END "
          + "FROM GeoSegment gs WHERE gs.woeid = ?1 AND gs.name LIKE ?2 AND gs.type=0")
  boolean existsCountryByWoeIdAndName(Long woeId, String name);
}
