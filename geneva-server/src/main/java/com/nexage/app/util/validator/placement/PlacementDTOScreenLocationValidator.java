package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.ScreenLocation.ABOVE_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.BELOW_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.FOOTER_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.FULLSCREEN_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.HEADER_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.SIDEBAR_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.UNKNOWN;
import static java.util.Objects.isNull;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.EnumSet;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;

public class PlacementDTOScreenLocationValidator
    extends BaseValidator<PlacementDTOScreenLocationConstraint, PlacementDTO> {
  private static final Set<ScreenLocation> STATIC_TYPES =
      EnumSet.of(
          UNKNOWN,
          ABOVE_VISIBLE,
          BELOW_VISIBLE,
          HEADER_VISIBLE,
          FOOTER_VISIBLE,
          SIDEBAR_VISIBLE,
          FULLSCREEN_VISIBLE);
  private static final Set<ScreenLocation> VIDEO_TYPES = EnumSet.of(FULLSCREEN_VISIBLE);
  private static final Set<ScreenLocation> IN_FEED_IN_ARTICLE_TYPES = EnumSet.of(UNKNOWN);

  @Override
  public boolean isValid(PlacementDTO placementDTO, ConstraintValidatorContext context) {

    PlacementCategory placementCategory = placementDTO.getPlacementCategory();
    ScreenLocation screenLocation = placementDTO.getScreenLocation();

    boolean result = false;

    if (isNull(placementCategory)) {
      ValidationUtils.addConstraintMessage(
          context, "placementCategory", getAnnotation().emptyMessage());
      return false;
    }

    if (isNull(screenLocation)) {
      return true;
    }

    switch (placementCategory) {
      case INSTREAM_VIDEO:
      case INTERSTITIAL:
      case REWARDED_VIDEO:
        result = VIDEO_TYPES.contains(screenLocation);
        break;
      case BANNER:
      case MEDIUM_RECTANGLE:
        result = STATIC_TYPES.contains(screenLocation);
        break;
      case IN_ARTICLE:
        // intentional fallthrough as same behaviour is expected in both In Article and In Feed
      case IN_FEED:
        result = IN_FEED_IN_ARTICLE_TYPES.contains(screenLocation);
        break;
    }
    if (!result) {
      ValidationUtils.addConstraintMessage(
          context, getAnnotation().field(), getAnnotation().message());
    }
    return result;
  }
}
