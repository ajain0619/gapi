package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.AdSizeType.CUSTOM;
import static com.nexage.admin.core.enums.AdSizeType.DYNAMIC;
import static com.nexage.admin.core.enums.AdSizeType.STANDARD;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE_V2;

import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;

/** Validator contains validation logic for AdSizeType */
public class PlacementDTOAdSizeTypeValidator
    extends BaseValidator<PlacementDTOAdSizeTypeConstraint, PlacementDTO> {
  private static Set<AdSizeType> BANNER_TYPES = EnumSet.of(CUSTOM, DYNAMIC, STANDARD);
  private static Set<AdSizeType> VIDEO_TYPES = EnumSet.of(CUSTOM, STANDARD);
  private static Set<PlacementCategory> SIZE_NOT_MANDATORY_CATEGORIES =
      EnumSet.of(IN_ARTICLE, IN_FEED);

  @Override
  public boolean isValid(PlacementDTO placementDTO, ConstraintValidatorContext context) {

    PlacementCategory placementCategory = placementDTO.getPlacementCategory();
    AdSizeType adSizeType = placementDTO.getAdSizeType();
    boolean result = false;

    if (!ValidationUtils.validateObjectNotNull(
        context, placementCategory, "placementCategory", getAnnotation().emptyMessage())) {
      return false;
    }

    if (SIZE_NOT_MANDATORY_CATEGORIES.contains(placementCategory)) {
      return true;
    }

    if (NATIVE_V2.equals(placementCategory) && Objects.isNull(adSizeType)) {
      return true;
    } else {
      switch (placementCategory) {
        case BANNER:
          result = BANNER_TYPES.contains(adSizeType);
          break;
        case INSTREAM_VIDEO:
        case INTERSTITIAL:
        case REWARDED_VIDEO:
          result = VIDEO_TYPES.contains(adSizeType);
          break;
        case MEDIUM_RECTANGLE:
          result = CUSTOM.equals(adSizeType);
          break;
      }
      if (!result) {
        ValidationUtils.addConstraintMessage(context, "adSizeType", getAnnotation().message());
      }
    }
    return result;
  }
}
