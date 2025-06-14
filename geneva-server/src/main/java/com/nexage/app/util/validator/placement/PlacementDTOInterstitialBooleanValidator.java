package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.INTERSTITIAL;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import javax.validation.ConstraintValidatorContext;

/** Validator contains validation logic for Interstitial fields */
public class PlacementDTOInterstitialBooleanValidator
    extends BaseValidator<PlacementDTOInterstitialBooleanConstraint, PlacementDTO> {

  @Override
  public boolean isValid(PlacementDTO placementDTO, ConstraintValidatorContext context) {

    boolean result = false;

    PlacementCategory placementCategory = placementDTO.getPlacementCategory();
    Boolean interstitial = placementDTO.getInterstitial();

    if (!ValidationUtils.validateObjectNotNull(
        context, placementCategory, "placementCategory", getAnnotation().emptyMessage())) {
      return false;
    }

    if (!ValidationUtils.validateObjectNotNull(
        context, interstitial, getAnnotation().field(), getAnnotation().emptyMessage())) {
      return false;
    }

    if (interstitial && INTERSTITIAL.equals(placementCategory)) {
      result = true;
    } else if (!interstitial && !INTERSTITIAL.equals(placementCategory)) {
      result = true;
    }

    if (!result) {
      ValidationUtils.addConstraintMessage(
          context, getAnnotation().field(), getAnnotation().message());
    }
    return result;
  }
}
