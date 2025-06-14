package com.nexage.app.util.validator;

import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.mapper.site.SiteTypeMapper;
import com.nexage.app.util.validator.placement.PlacementDTODoohValidator;
import javax.validation.ConstraintValidatorContext;

public class PublisherPositionDTODoohValidator
    extends BaseValidator<DoohConstraint, PublisherPositionDTO> {

  @Override
  public boolean isValid(
      PublisherPositionDTO publisherPositionDTO, ConstraintValidatorContext context) {

    if (publisherPositionDTO.getSite() == null
        || publisherPositionDTO.getSite().getType() == null) {
      return true;
    }

    if (PlacementDTODoohValidator.isValid(
        SiteTypeMapper.MAPPER.map(publisherPositionDTO.getSite().getType()),
        publisherPositionDTO.getDooh())) {
      return true;
    }
    ValidationUtils.addConstraintMessage(context, "dooh", getAnnotation().message());
    return false;
  }
}
