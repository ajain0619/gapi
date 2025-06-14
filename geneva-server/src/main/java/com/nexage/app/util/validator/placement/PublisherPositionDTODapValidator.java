package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.mapper.site.SiteTypeMapper;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.Objects;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class PublisherPositionDTODapValidator
    extends BaseValidator<PlacementDTODapConstraint, PublisherPositionDTO> {

  private PlacementDTODapValidator placementDTODapValidator;

  @Autowired
  public PublisherPositionDTODapValidator(PlacementDTODapValidator placementDTODapValidator) {
    this.placementDTODapValidator = placementDTODapValidator;
  }

  @Override
  public boolean isValid(
      PublisherPositionDTO publisherPositionDTO, ConstraintValidatorContext context) {

    PublisherSiteDTO.SiteType siteType =
        Optional.ofNullable(publisherPositionDTO.getSite())
            .map(PublisherSiteDTO::getType)
            .orElse(null);
    PlacementCategory placementCategory = publisherPositionDTO.getPlacementCategory();
    VideoSupport videoSupport = publisherPositionDTO.getVideoSupport();
    PlacementVideoDTO placementVideoDTO = publisherPositionDTO.getPlacementVideo();

    if (Objects.isNull(placementVideoDTO)) {
      return true;
    }

    if (!ValidationUtils.validateAllObjectsNotNull(
        context,
        getAnnotation().field(),
        getAnnotation().emptyMessage(),
        placementCategory,
        siteType)) {
      return false;
    }

    if (videoSupport != VIDEO_AND_BANNER) {
      return true;
    }

    return placementDTODapValidator.isPlacementDapCompatible(
        placementCategory,
        videoSupport,
        SiteTypeMapper.MAPPER.map(siteType),
        placementVideoDTO,
        context);
  }
}
