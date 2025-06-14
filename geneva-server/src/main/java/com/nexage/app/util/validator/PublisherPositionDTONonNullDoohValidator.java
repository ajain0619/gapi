package com.nexage.app.util.validator;

import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.mapper.site.SiteTypeMapper;
import com.nexage.app.util.validator.placement.PlacementDTONonNullDoohValidator;
import javax.validation.ConstraintValidatorContext;

public class PublisherPositionDTONonNullDoohValidator
    extends BaseValidator<NonNullDoohConstraint, PublisherPositionDTO> {

  @Override
  public boolean isValid(
      PublisherPositionDTO publisherPositionDTO, ConstraintValidatorContext context) {
    if (publisherPositionDTO.getSite() == null) {
      return true;
    }
    if (PlacementDTONonNullDoohValidator.isNotValid(
        SiteTypeMapper.MAPPER.map(publisherPositionDTO.getSite().getType()),
        publisherPositionDTO.getDooh())) {
      return addConstraintMessage(context, "dooh", getAnnotation().message());
    }
    return true;
  }
}
