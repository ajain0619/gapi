package com.nexage.app.services;

import com.nexage.app.dto.seller.PlacementValidMemoDTO;

public interface PlacementValidMemoDTOService {
  /**
   * Return one {@link PlacementValidMemoDTO} under request criteria, returning a single
   * PlacementValidNameDTO response.
   *
   * @param siteId siteId
   * @param sellerId sellerId
   * @param memo placement memo
   * @return {@link PlacementValidMemoDTO} instance based on parameters.
   */
  PlacementValidMemoDTO getValidPlacementMemo(Long siteId, Long sellerId, String memo);
}
