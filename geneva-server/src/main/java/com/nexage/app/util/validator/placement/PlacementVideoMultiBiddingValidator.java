package com.nexage.app.util.validator.placement;

import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.BaseValidator;
import javax.validation.ConstraintValidatorContext;

public class PlacementVideoMultiBiddingValidator
    extends BaseValidator<PlacementVideoMultiBiddingConstraint, PlacementVideoDTO> {
  @Override
  public boolean isValid(PlacementVideoDTO placementVideoDTO, ConstraintValidatorContext context) {

    boolean longform = placementVideoDTO.isLongform();
    boolean multiImpressionBid = placementVideoDTO.isMultiImpressionBid();
    boolean competitiveSeparation = placementVideoDTO.isCompetitiveSeparation();
    var result = true;

    if (!longform && multiImpressionBid) {
      result =
          addConstraintMessage(
              context, ServerErrorCodes.SERVER_INVALID_VIDEO_MULTI_IMPRESSION_BID.toString());
    }
    if (!multiImpressionBid && competitiveSeparation) {
      result =
          addConstraintMessage(
              context, ServerErrorCodes.SERVER_INVALID_VIDEO_COMPETITIVE_SEPARATION.toString());
    }

    return result;
  }

  private boolean addConstraintMessage(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    super.buildConstraintViolationWithTemplate(context, message);
    return false;
  }
}
