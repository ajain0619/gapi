package com.nexage.app.services;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;

public interface NativePlacementHbPartnerService {
  /**
   * performs the logic to map from NativePlacementDTO hbPartnerAttributes to Position
   * hbPartnerPosition
   *
   * @param nativePlacement - Native Placement DTO object (source)
   * @param position - Position object to be mapped to (target)
   * @param site - Site of the placement
   */
  void handleHbPartnersAssignmentMapping(
      NativePlacementDTO nativePlacement, Position position, Site site);

  /**
   * performs the logic to map from Position hbPartnerPosition to NativePlacementDTO *
   * hbPartnerAttributes
   *
   * @param nativePlacement - Native Placement DTO object (target)
   * @param position Position object to be mapped to (source)
   */
  void handleHbPartnerPositionMapping(NativePlacementDTO nativePlacement, Position position);
}
