package com.nexage.app.services;

import com.nexage.app.dto.seller.PlacementSummaryDTO;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlacementSummaryDTOService {

  /**
   * Find all {@link PlacementSummaryDTO} under request criteria, returning a paginated response.
   *
   * @param siteId siteId
   * @param sellerId sellerId
   * @param startDate start
   * @param stopDate stop
   * @param name name
   * @param placementId placementId
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link PlacementSummaryDTO} instances based on parameters.
   */
  Page<PlacementSummaryDTO> getPlacementsWithMetrics(
      Date startDate,
      Date stopDate,
      Long siteId,
      Long sellerId,
      Optional<String> name,
      Optional<Long> placementId,
      Pageable pageable);

  /**
   * Find all {@link PlacementSummaryDTO} under request criteria, returning a paginated response.
   *
   * @param sellerId sellerId
   * @param startDate start
   * @param stopDate stop
   * @param name name
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link PlacementSummaryDTO} instances based on parameters.
   */
  Page<PlacementSummaryDTO> getPlacementsWithMetricsWithoutSitePid(
      Date startDate, Date stopDate, Long sellerId, String name, Pageable pageable);
}
