package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;

import com.nexage.admin.core.enums.MRAIDSupport;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintValidatorContext;

public class PublisherPositionDTOMRAIDSupportValidator
    extends BaseValidator<PlacementDTOMRAIDSupportConstraint, PublisherPositionDTO> {

  private static List<PlacementCategory> NOT_SUPPORTED_TYPES = Arrays.asList(IN_ARTICLE, IN_FEED);

  @Override
  public boolean isValid(
      PublisherPositionDTO publisherPositionDTO, ConstraintValidatorContext context) {

    PlacementCategory placementCategory = publisherPositionDTO.getPlacementCategory();
    MRAIDSupport mraidSupport = publisherPositionDTO.getMraidSupport();

    boolean result = true;

    if (!ValidationUtils.validateObjectNotNull(
        context, placementCategory, "placementCategory", getAnnotation().emptyMessage())) {
      return false;
    }

    if (NOT_SUPPORTED_TYPES.contains(placementCategory) && MRAIDSupport.YES.equals(mraidSupport)) {
      result = false;
    }

    if (!result) {
      ValidationUtils.addConstraintMessage(
          context, getAnnotation().field(), getAnnotation().message());
    }

    return result;
  }
}
