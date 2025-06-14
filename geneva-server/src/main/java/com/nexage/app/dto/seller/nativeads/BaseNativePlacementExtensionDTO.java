package com.nexage.app.dto.seller.nativeads;

import static com.nexage.app.util.validator.ValidationMessages.WRONG_ELEMENT_IS_EMPTY;
import static com.nexage.app.util.validator.ValidationMessages.WRONG_IS_EMPTY;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nexage.admin.core.enums.Context;
import com.nexage.admin.core.enums.ContextSubType;
import com.nexage.admin.core.enums.PlacementType;
import com.nexage.app.dto.seller.nativeads.asset.NativeAssetSetDTO;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize(using = NativePlacementExtensionDTODeserializer.class)
public abstract class BaseNativePlacementExtensionDTO {

  private Context context;
  private ContextSubType contextSubType;
  private PlacementType placementType;

  @NotNull(message = WRONG_IS_EMPTY)
  private String assetTemplate;

  @Valid
  @Size(min = 1, message = WRONG_ELEMENT_IS_EMPTY)
  protected Set<NativeAssetSetDTO> assetSets;
}
