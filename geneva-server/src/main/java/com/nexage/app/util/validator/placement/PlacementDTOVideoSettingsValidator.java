package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.VideoSupport.VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.BooleanUtils;

public class PlacementDTOVideoSettingsValidator
    extends BaseValidator<PlacementDTOVideoSettingsConstraint, PlacementDTO> {

  /**
   * Validates video settings when VideoSupport is of type VIDEO.
   *
   * @param placementDTO {@link PlacementDTO}
   * @param context {@link ConstraintValidatorContext}
   * @return true if valid
   */
  @Override
  public boolean isValid(PlacementDTO placementDTO, ConstraintValidatorContext context) {

    PlacementCategory placementCategory = placementDTO.getPlacementCategory();
    VideoSupport videoSupport = placementDTO.getVideoSupport();
    PlacementVideoDTO placementVideoDTO = placementDTO.getPlacementVideo();

    if (videoSupport == null) {
      return true;
    }

    if (!ValidationUtils.validateObjectNotNull(
            context, placementCategory, "placementCategory", getAnnotation().emptyMessage())
        || !ValidationUtils.validateObjectNotNull(
            context, videoSupport, "videoSupport", getAnnotation().emptyMessage())) {
      return false;
    }

    if (videoSupport != VIDEO && videoSupport != VIDEO_AND_BANNER) {
      return true;
    }

    return validateVideoParamsInPlacementVideoDTO(placementCategory, placementVideoDTO, context);
  }

  private boolean validateVideoParamsInPlacementVideoDTO(
      PlacementCategory placementCategory,
      PlacementVideoDTO placementVideoDTO,
      ConstraintValidatorContext context) {

    if (BooleanUtils.isTrue(placementVideoDTO.isLongform())
        && !ValidationUtils.validateIntEquals(
            context,
            placementCategory.asInt(),
            PlacementCategory.INSTREAM_VIDEO.asInt(),
            "placementCategory",
            getAnnotation().invalidPlacementCategoryForLongform())) {
      return false;
    }
    return true;
  }
}
