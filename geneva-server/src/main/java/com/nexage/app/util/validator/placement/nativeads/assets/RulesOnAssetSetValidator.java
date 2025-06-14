package com.nexage.app.util.validator.placement.nativeads.assets;

import static com.nexage.admin.core.enums.nativeads.NativeAssetRule.REQ_ALL;
import static com.nexage.admin.core.enums.nativeads.NativeAssetRule.REQ_NONE;

import com.nexage.admin.core.enums.nativeads.NativeAssetRule;
import com.nexage.app.dto.seller.nativeads.WebNativePlacementExtensionDTO;
import com.nexage.app.dto.seller.nativeads.asset.NativeAssetSetDTO;
import com.nexage.app.util.validator.BaseValidator;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;

/**
 * This Validator: 1) REQ_ALL rule must exists as a rule 2) if more than one asset set, then
 * REQ_NONE rule must exists
 */
public class RulesOnAssetSetValidator
    extends BaseValidator<RulesOnAssetSet, WebNativePlacementExtensionDTO> {

  @Override
  public boolean isValid(
      WebNativePlacementExtensionDTO webNativePlacementExtension,
      ConstraintValidatorContext constraintValidatorContext) {
    boolean isValid = true;
    Set<NativeAssetSetDTO> assetSets = webNativePlacementExtension.getAssetSets();
    if (!isReqAllAssetSetExists(assetSets)
        || assetSets.size() > 1 && !isReqNoneAssetSetExists(assetSets)) {
      isValid = false;
    }
    return isValid;
  }

  private boolean isReqNoneAssetSetExists(Set<NativeAssetSetDTO> assetSets) {
    return isSetExistsByRule(assetSets, REQ_NONE);
  }

  private boolean isReqAllAssetSetExists(Set<NativeAssetSetDTO> assetSets) {
    return isSetExistsByRule(assetSets, REQ_ALL);
  }

  private boolean isSetExistsByRule(Set<NativeAssetSetDTO> assetSets, NativeAssetRule rule) {
    return assetSets.stream().anyMatch(assetSet -> assetSet.getRule().equals(rule));
  }
}
