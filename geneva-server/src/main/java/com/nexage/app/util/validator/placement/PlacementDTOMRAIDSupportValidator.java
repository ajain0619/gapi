package com.nexage.app.util.validator.placement;

import com.nexage.admin.core.enums.MRAIDSupport;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintValidatorContext;

/** Validator contains validation logic for MRAIDSupport field */
public class PlacementDTOMRAIDSupportValidator
    extends BaseValidator<PlacementDTOMRAIDSupportConstraint, PlacementDTO> {
  private static List<PlacementCategory> NOT_SUPPORTED_TYPES =
      Arrays.asList(
          PlacementCategory.REWARDED_VIDEO,
          PlacementCategory.INSTREAM_VIDEO,
          PlacementCategory.IN_ARTICLE,
          PlacementCategory.IN_FEED);

  @Override
  public boolean isValid(PlacementDTO placement, ConstraintValidatorContext context) {

    PlacementCategory placementCategory = placement.getPlacementCategory();
    MRAIDSupport mraidSupport = placement.getMraidSupport();

    boolean result = false;

    if (!ValidationUtils.validateObjectNotNull(
        context, placementCategory, "placementCategory", getAnnotation().emptyMessage())) {
      return false;
    }

    if (((MRAIDSupport.YES.equals(mraidSupport) && !NOT_SUPPORTED_TYPES.contains(placementCategory))
        || (MRAIDSupport.NO.equals(mraidSupport) && NOT_SUPPORTED_TYPES.contains(placementCategory))
        || mraidSupport == null)) {
      result = true;
    }

    if (!result) {
      ValidationUtils.addConstraintMessage(
          context, getAnnotation().field(), getAnnotation().message());
    }

    return result;
  }
}
