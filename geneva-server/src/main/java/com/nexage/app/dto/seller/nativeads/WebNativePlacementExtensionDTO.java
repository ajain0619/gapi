package com.nexage.app.dto.seller.nativeads;

import static com.nexage.app.util.validator.ValidationMessages.WRONG_IS_EMPTY;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nexage.app.dto.seller.nativeads.asset.NativeAssetSetDTO;
import com.nexage.app.util.validator.placement.nativeads.assets.RulesOnAssetSet;
import com.nexage.app.util.validator.placement.nativeads.assets.ValidAssetsWithTemplate;
import com.nexage.app.util.validator.placement.nativeads.decoder.ValidHtmlTemplate;
import com.nexage.app.util.validator.placement.nativeads.xpath.ValidXPath;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@JsonDeserialize(as = WebNativePlacementExtensionDTO.class)
@RulesOnAssetSet
@ValidAssetsWithTemplate
public class WebNativePlacementExtensionDTO extends BaseNativePlacementExtensionDTO {

  @NotNull(message = WRONG_IS_EMPTY)
  @ValidHtmlTemplate
  private String renderingTemplate;

  @NotNull(message = WRONG_IS_EMPTY)
  @ValidXPath
  private String adXPath;

  @Size(min = 1, max = 2)
  public Set<NativeAssetSetDTO> getAssetSets() {
    return assetSets;
  }
}
