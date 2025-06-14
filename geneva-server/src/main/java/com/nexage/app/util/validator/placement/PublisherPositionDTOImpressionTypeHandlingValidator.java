package com.nexage.app.util.validator.placement;

import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.util.validator.BaseValidator;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;

public class PublisherPositionDTOImpressionTypeHandlingValidator
    extends BaseValidator<PlacementDTOImpressionTypeHandlingConstraint, PublisherPositionDTO> {

  @Override
  public boolean isValid(
      PublisherPositionDTO publisherPositionDTO, ConstraintValidatorContext context) {
    var siteType =
        Optional.ofNullable(publisherPositionDTO.getSite())
            .map(PublisherSiteDTO::getType)
            .orElse(null);

    return PlacementDTOImpressionTypeHandlingValidator.isValid(
        siteType,
        publisherPositionDTO.getPlacementCategory(),
        publisherPositionDTO.getVideoSupport(),
        publisherPositionDTO.getImpressionTypeHandling(),
        context,
        getAnnotation());
  }
}
