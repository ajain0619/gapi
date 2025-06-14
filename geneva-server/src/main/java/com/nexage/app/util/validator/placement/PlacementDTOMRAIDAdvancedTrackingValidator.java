package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.PlacementCategory.INTERSTITIAL;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE_V2;
import static com.nexage.admin.core.enums.PlacementCategory.REWARDED_VIDEO;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.validation.ConstraintValidatorContext;

public class PlacementDTOMRAIDAdvancedTrackingValidator
    extends BaseValidator<PlacementDTOMRAIDAdvancedTrackingConstraint, PlacementDTO> {
  private static List<PlacementCategory> SUPPORTED_TYPES =
      Arrays.asList(INTERSTITIAL, INSTREAM_VIDEO, REWARDED_VIDEO, NATIVE_V2, IN_ARTICLE, IN_FEED);

  @Override
  public boolean isValid(PlacementDTO placement, ConstraintValidatorContext context) {

    PlacementCategory placementCategory = placement.getPlacementCategory();
    boolean MRAIDAdvancedTracking = placement.getMraidAdvancedTracking();

    if (Objects.isNull(placementCategory)) {
      ValidationUtils.addConstraintMessage(
          context, "placementCategory", getAnnotation().emptyMessage());
      return false;
    }

    if (!SUPPORTED_TYPES.contains(placementCategory) && MRAIDAdvancedTracking) {
      ValidationUtils.addConstraintMessage(
          context, getAnnotation().field(), getAnnotation().message());
      return false;
    }

    return true;
  }
}
