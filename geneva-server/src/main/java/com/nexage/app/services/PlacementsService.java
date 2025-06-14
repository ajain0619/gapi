package com.nexage.app.services;

import com.nexage.app.dto.seller.PlacementDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlacementsService {
  /**
   * Find all {@link PlacementDTO} under request criteria, returning a paginated response.
   *
   * @param sitePid sitePid
   * @param sellerPid sellerPid
   * @param qt The term to be found.
   * @param placementType placementType
   * @param status status
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link PlacementDTO} instances based on parameters.
   */
  Page<PlacementDTO> getPlacements(
      Pageable pageable,
      Optional<Long> sitePid,
      Long sellerPid,
      Optional<String> qt,
      Optional<List<String>> placementType,
      Optional<List<String>> status);

  /**
   * @param pageable Pagination based on {@link Pageable}
   * @param sitePid sitePid
   * @param sellerPid sellerPid
   * @param qt The term to be found.
   * @return {@link Page} of {@link PlacementDTO} instances based on parameters.
   */
  Page<PlacementDTO> getPlacementsMinimalData(
      Pageable pageable, Long sitePid, Long sellerPid, String qt);

  /**
   * Save a {@link PlacementDTO}
   *
   * @param sellerPid {@link Long}
   * @param placementDTO {@link PlacementDTO}
   * @return {@link PlacementDTO}
   */
  PlacementDTO save(Long sellerPid, PlacementDTO placementDTO);

  /**
   * Update an existing {@link PlacementDTO}
   *
   * @param sellerPid {@link Long}
   * @param placementDTO {@link PlacementDTO}
   * @return {@link PlacementDTO}
   */
  PlacementDTO update(Long sellerPid, PlacementDTO placementDTO);
}
