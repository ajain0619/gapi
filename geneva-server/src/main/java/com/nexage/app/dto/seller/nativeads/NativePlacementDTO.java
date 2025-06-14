package com.nexage.app.dto.seller.nativeads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.dto.seller.PlacementDTO;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class NativePlacementDTO extends PlacementDTO {

  @Valid private BaseNativePlacementExtensionDTO nativePlacementExtension;
  private Set<PublisherTagDTO> tags;
  private Set<PublisherTierDTO> tiers;
  private PlacementBuyerDTO placementBuyer;
  private Set<HbPartnerAssignmentDTO> hbPartnerAttributes;

  @Override
  @Null
  public Integer getWidth() {
    return super.getWidth();
  }

  @Override
  @Null
  public Integer getHeight() {
    return super.getHeight();
  }
}
