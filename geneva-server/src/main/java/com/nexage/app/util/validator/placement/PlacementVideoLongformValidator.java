package com.nexage.app.util.validator.placement;

import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationMessages;
import java.util.Objects;
import javax.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

public class PlacementVideoLongformValidator
    extends BaseValidator<PlacementVideoLongformConstraint, PlacementVideoDTO> {

  private static final String PLAYER_BRAND_PATTERN = "^[A-Za-z0-9._~\\-]+$";
  public static final int MAX_PLAYER_BRAND_LENGTH = 100;

  /**
   * Validates longform fields in PlacementVideoDTO.
   *
   * @param placementVideoDTO {@link PlacementVideoDTO}
   * @param context {@link ConstraintValidatorContext}
   * @return true if valid
   */
  public boolean isValid(PlacementVideoDTO placementVideoDTO, ConstraintValidatorContext context) {

    boolean longform = placementVideoDTO.isLongform();
    PlacementVideoStreamType streamType = placementVideoDTO.getStreamType();
    String playerBrand = placementVideoDTO.getPlayerBrand();
    PlacementVideoSsai ssai = placementVideoDTO.getSsai();
    boolean result = true;

    if (longform && !CollectionUtils.isEmpty(placementVideoDTO.getCompanions())) {

      result =
          addConstraintMessage(
              context, ValidationMessages.PLACEMENT_VIDEO_LONGFORM_CONSTRAINT_VIOLATION);
    }

    if (!isStreamTypeSsaiValid(longform, streamType, ssai, context)) {
      result = false;
    }

    if (!Objects.isNull(playerBrand) && !isPlayerBrandValid(longform, playerBrand)) {
      result =
          addConstraintMessage(
              context, ValidationMessages.PLACEMENT_VIDEO_PLAYER_BRAND_CONSTRAINT_VIOLATION);
    }

    return result;
  }

  private boolean isPlayerBrandValid(boolean longform, String playerBrand) {
    return !(!longform
        || playerBrand.length() > MAX_PLAYER_BRAND_LENGTH
        || !playerBrand.matches(PLAYER_BRAND_PATTERN));
  }

  private boolean isStreamTypeSsaiValid(
      boolean longform,
      PlacementVideoStreamType streamType,
      PlacementVideoSsai ssai,
      ConstraintValidatorContext context) {
    boolean result = true;
    if (longform) {
      if (Objects.isNull(streamType)) {
        result =
            addConstraintMessage(
                context, ValidationMessages.PLACEMENT_VIDEO_STREAM_TYPE_CONSTRAINT_VIOLATION);
      }
      if (Objects.isNull(ssai)) {
        result =
            addConstraintMessage(
                context, ValidationMessages.PLACEMENT_VIDEO_SSAI_CONSTRAINT_VIOLATION);
      }
    } else {
      if (!Objects.isNull(streamType)) {
        result =
            addConstraintMessage(
                context, ValidationMessages.PLACEMENT_VIDEO_STREAM_TYPE_CONSTRAINT_VIOLATION);
      }
      if (!Objects.isNull(ssai)) {
        result =
            addConstraintMessage(
                context, ValidationMessages.PLACEMENT_VIDEO_SSAI_CONSTRAINT_VIOLATION);
      }
    }
    return result;
  }

  private boolean addConstraintMessage(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    super.buildConstraintViolationWithTemplate(context, message);
    return false;
  }
}
