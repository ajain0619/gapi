package com.nexage.app.dto.seller.nativeads.asset;

import static com.nexage.app.util.validator.ValidationMessages.WRONG_ELEMENT_IS_EMPTY;
import static com.nexage.app.util.validator.ValidationMessages.WRONG_IS_EMPTY;

import com.nexage.admin.core.enums.nativeads.NativeAssetRule;
import com.nexage.app.dto.seller.nativeads.asset.type.NativeAssetDTO;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NativeAssetSetDTO {
  @NotNull(message = WRONG_IS_EMPTY)
  private NativeAssetRule rule;

  @Valid
  @NotNull(message = WRONG_ELEMENT_IS_EMPTY)
  private Set<NativeAssetDTO> assets;
}
