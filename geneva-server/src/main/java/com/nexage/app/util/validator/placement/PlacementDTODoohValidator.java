package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.site.Type.DOOH;
import static java.util.Objects.isNull;

import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.DoohConstraint;
import com.nexage.app.util.validator.ValidationUtils;
import javax.validation.ConstraintValidatorContext;

public class PlacementDTODoohValidator extends BaseValidator<DoohConstraint, PlacementDTO> {

  @Override
  public boolean isValid(
      PlacementDTO placementDTO, ConstraintValidatorContext constraintValidatorContext) {

    Type siteType = placementDTO.getSite().getType();
    PlacementDoohDTO placementDoohDTO = placementDTO.getDooh();

    if (isValid(siteType, placementDoohDTO)) {
      return true;
    } else {
      ValidationUtils.addConstraintMessage(
          constraintValidatorContext, "dooh", getAnnotation().message());
      return false;
    }
  }

  /**
   * Checks given siteType whether the placement supports DOOH object.
   *
   * @param siteType {@link Type} siteType
   * @param placementDoohDTO {@link PlacementDoohDTO} placementDoohDTO to verify
   * @return {@link boolean} true if valid dooh placement, false otherwise
   */
  public static boolean isValid(Type siteType, PlacementDoohDTO placementDoohDTO) {
    return DOOH == siteType || isNull(placementDoohDTO);
  }
}
