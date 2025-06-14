package com.nexage.app.services;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;

public interface NativePlacementTierService {

  /**
   * update position according to nativePlacementDTO tiers
   *
   * @param nativePlacementDTO
   * @param site
   * @param position
   */
  void update(NativePlacementDTO nativePlacementDTO, Site site, Position position);
}
