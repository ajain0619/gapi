package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.PlacementCategory.MEDIUM_RECTANGLE;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE_V2;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;

/** Validator contains validation logic for width, height fields */
public class PlacementDTOAdSizeValidator
    extends BaseValidator<PlacementDTOAdSizeConstraint, PlacementDTO> {
  private static ImmutableCollection<String> BANNER_SIZES =
      ImmutableList.of("300,50", "320,50", "728,90");
  private static ImmutableCollection<String> INTERSTITIAL_SIZES =
      ImmutableList.of("300,250", "320,280", "480,320", "1024,768", "768,1024");
  private static ImmutableCollection<String> INSTREAM_VIDEO_SIZES =
      ImmutableList.of("1920,1080", "1080,1920", "1400,400");
  private static Set<PlacementCategory> EMPTY_SIZE_CAPABLE =
      EnumSet.of(MEDIUM_RECTANGLE, NATIVE_V2);
  private static Set<PlacementCategory> SIZE_NOT_MANDATORY_CATEGORIES =
      EnumSet.of(IN_ARTICLE, IN_FEED);

  @Override
  public boolean isValid(PlacementDTO placementDTO, ConstraintValidatorContext context) {

    PlacementCategory placementCategory = placementDTO.getPlacementCategory();
    AdSizeType adSizeType = placementDTO.getAdSizeType();
    Integer width = placementDTO.getWidth();
    Integer height = placementDTO.getHeight();
    String wh = width + "," + height;
    boolean result = false;

    if (!ValidationUtils.validateObjectNotNull(
        context, placementCategory, "placementCategory", getAnnotation().emptyMessage())) {
      return false;
    }

    if (SIZE_NOT_MANDATORY_CATEGORIES.contains(placementCategory)) {
      return true;
    }

    if (!NATIVE_V2.equals(placementCategory)
        && !ValidationUtils.validateObjectNotNull(
            context, adSizeType, "adSizeType", getAnnotation().emptyMessage())) {
      return false;
    }

    if (EMPTY_SIZE_CAPABLE.contains(placementCategory)
        && Objects.isNull(width)
        && Objects.isNull(height)) {
      result = true;
    } else if (!(Objects.isNull(width) || Objects.isNull(height))) {
      result = true;
    } else {
      ValidationUtils.addConstraintMessage(context, "adSize", getAnnotation().message());
      return false;
    }

    if (AdSizeType.STANDARD.equals(adSizeType)) {
      switch (placementCategory) {
        case BANNER:
          result = BANNER_SIZES.contains(wh);
          break;
        case INTERSTITIAL:
          result = INTERSTITIAL_SIZES.contains(wh);
          break;
        case INSTREAM_VIDEO:
          result = INSTREAM_VIDEO_SIZES.contains(wh);
          break;
      }
      if (!result) {
        ValidationUtils.addConstraintMessage(context, "adSize", getAnnotation().message());
      }
    }
    return result;
  }
}
