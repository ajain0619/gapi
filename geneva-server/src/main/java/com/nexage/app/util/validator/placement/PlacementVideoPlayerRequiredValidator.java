package com.nexage.app.util.validator.placement;

import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.util.validator.BaseValidator;
import java.util.Objects;
import javax.validation.ConstraintValidatorContext;

public class PlacementVideoPlayerRequiredValidator
    extends BaseValidator<PlacementVideoPlayerRequiredConstraint, PlacementVideoDTO> {

  /**
   * Validates player height and width if playerRequired is TRUE
   *
   * @param placementVideoDTO {@link PlacementVideoDTO}
   * @param context {@link ConstraintValidatorContext}
   * @return true if valid
   */
  @Override
  public boolean isValid(PlacementVideoDTO placementVideoDTO, ConstraintValidatorContext context) {
    if (!placementVideoDTO.isPlayerRequired()) {
      return true;
    }

    if (Objects.nonNull(placementVideoDTO.getVideoPlacementType())
        || Objects.nonNull(placementVideoDTO.getDapPlayerType())) {
      return true;
    }

    return !(Objects.isNull(placementVideoDTO.getPlayerHeight())
        || Objects.isNull(placementVideoDTO.getPlayerWidth()));
  }
}
