package com.nexage.app.util.validator.placement;

import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.NonNullDoohConstraint;
import java.util.Objects;
import javax.validation.ConstraintValidatorContext;

public class PlacementDTONonNullDoohValidator
    extends BaseValidator<NonNullDoohConstraint, PlacementDTO> {
  @Override
  public boolean isValid(
      PlacementDTO placementDTO, ConstraintValidatorContext constraintValidatorContext) {

    Type siteType = placementDTO.getSite().getType();
    PlacementDoohDTO placementDoohDTO = placementDTO.getDooh();

    if (isNotValid(siteType, placementDoohDTO)) {
      return addConstraintMessage(constraintValidatorContext, "dooh", getAnnotation().message());
    }
    return true;
  }

  public static boolean isNotValid(Type siteType, PlacementDoohDTO placementDoohDTO) {
    return Type.DOOH.equals(siteType) && Objects.isNull(placementDoohDTO);
  }
}
