package com.nexage.app.util.validator.placement;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.mapper.site.PlatformTypeMapper;
import com.nexage.app.mapper.site.SiteTypeMapper;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class PublisherPositionDTOVideoSupportValidator
    extends BaseValidator<PlacementDTOVideoSupportConstraint, PublisherPositionDTO> {

  private PlacementDTOVideoSupportValidator placementDTOVideoSupportValidator;

  @Autowired
  public PublisherPositionDTOVideoSupportValidator(
      PlacementDTOVideoSupportValidator placementDTOVideoSupportValidator) {
    this.placementDTOVideoSupportValidator = placementDTOVideoSupportValidator;
  }

  @Override
  public boolean isValid(
      PublisherPositionDTO publisherPositionDTO, ConstraintValidatorContext context) {

    PublisherSiteDTO.SiteType siteType =
        Optional.ofNullable(publisherPositionDTO.getSite())
            .map(PublisherSiteDTO::getType)
            .orElse(null);
    PublisherSiteDTO.Platform platformType =
        Optional.ofNullable(publisherPositionDTO.getSite())
            .map(PublisherSiteDTO::getPlatform)
            .orElse(null);
    PlacementCategory placementCategory = publisherPositionDTO.getPlacementCategory();
    VideoSupport videoSupport = publisherPositionDTO.getVideoSupport();

    if (!ValidationUtils.validateAllObjectsNotNull(
        context,
        getAnnotation().field(),
        getAnnotation().emptyMessage(),
        placementCategory,
        siteType,
        platformType)) {
      return false;
    }

    boolean result =
        placementDTOVideoSupportValidator.isVideoSupportValid(
            placementCategory,
            videoSupport,
            SiteTypeMapper.MAPPER.map(siteType),
            PlatformTypeMapper.MAPPER.map(platformType));

    if (!result) {
      ValidationUtils.addConstraintMessage(
          context, getAnnotation().field(), getAnnotation().message());
    }
    return result;
  }
}
