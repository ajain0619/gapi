package com.nexage.app.dto;

import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Representation of native placement request params .
 *
 * @author ystern
 */
@AllArgsConstructor
@Builder
@Data
public class NativePlacementRequestParamsDTO {

  private final Long sellerId;

  private final Long siteId;

  private final Long placementId;

  private final NativePlacementDTO nativePlacement;
}
