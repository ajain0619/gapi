package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.ScreenLocation.UNKNOWN;
import static java.util.Objects.isNull;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.EnumSet;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;

public class PublisherPositionDTOScreenLocationValidator
    extends BaseValidator<PlacementDTOScreenLocationConstraint, PublisherPositionDTO> {

  private static final Set<PlacementCategory> CATEGORIES_ALLOWED_FOR_UNKNOWN =
      EnumSet.of(IN_ARTICLE, IN_FEED);

  @Override
  public boolean isValid(
      PublisherPositionDTO publisherPositionDTO, ConstraintValidatorContext context) {

    PlacementCategory placementCategory = publisherPositionDTO.getPlacementCategory();
    ScreenLocation screenLocation = publisherPositionDTO.getScreenLocation();

    if (isNull(placementCategory)) {
      ValidationUtils.addConstraintMessage(
          context, "placementCategory", getAnnotation().emptyMessage());
      return false;
    }

    if (isNull(screenLocation)) {
      return true;
    }

    if (CATEGORIES_ALLOWED_FOR_UNKNOWN.contains(placementCategory) && screenLocation != UNKNOWN) {
      ValidationUtils.addConstraintMessage(
          context, getAnnotation().field(), getAnnotation().message());
      return false;
    }

    return true;
  }
}
