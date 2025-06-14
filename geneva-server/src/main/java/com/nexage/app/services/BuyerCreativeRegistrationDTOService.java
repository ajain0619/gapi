package com.nexage.app.services;

import com.nexage.app.dto.CreativeRegistrationDTO;
import com.ssp.geneva.common.error.exception.GenevaValidationException;

public interface BuyerCreativeRegistrationDTOService {
  /**
   * Submits the given creative for CRS review and registers the creative with the given sellers.
   *
   * @param buyerId The buyer owning the creative.
   * @param creativeId The creative id from CRS.
   * @param dto The DTO containing creative information and associated sellers.
   * @return The created creative data.
   * @throws GenevaValidationException If the creative is already registered.
   */
  CreativeRegistrationDTO registerCreativeAction(
      Long buyerId, String creativeId, CreativeRegistrationDTO dto);
}
