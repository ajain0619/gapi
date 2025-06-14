package com.nexage.app.services;

import com.nexage.app.dto.NativePlacementRequestParamsDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;

public interface NativePlacementDTOService {

  /**
   * @param nativePlacementRequestParams represent the call parameters
   * @return the created NativePlacementDTO
   */
  NativePlacementDTO createPlacement(NativePlacementRequestParamsDTO nativePlacementRequestParams);

  /**
   * @param sellerId sellerId represents the seller, also known as company id
   * @param siteId siteId represents the site
   * @param positionId the associated positionId
   * @return NativePlacementDTO associated with the given positionId
   */
  NativePlacementDTO getNativePlacementById(Long sellerId, Long siteId, Long positionId);

  /**
   * @param nativePlacementRequestParams represent the call parameters
   * @return the new updated NativePlacementDTO associated with the given positionId
   */
  NativePlacementDTO updatePlacement(NativePlacementRequestParamsDTO nativePlacementRequestParams);
}
