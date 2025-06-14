package com.nexage.app.util.validator.placement;

import com.nexage.admin.core.enums.ImpressionTypeHandling;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.util.placement.DapVideoPlacementUtil;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PlacementDTOImpressionTypeHandlingValidator
    extends BaseValidator<PlacementDTOImpressionTypeHandlingConstraint, PlacementDTO> {

  @Override
  public boolean isValid(PlacementDTO placementDTO, ConstraintValidatorContext context) {
    var siteType = Optional.ofNullable(placementDTO.getSite()).map(SiteDTO::getType).orElse(null);
    PublisherSiteDTO.SiteType pubSiteType =
        siteType != null ? PublisherSiteDTO.SiteType.valueOf(siteType.toString()) : null;

    return isValid(
        pubSiteType,
        placementDTO.getPlacementCategory(),
        placementDTO.getVideoSupport(),
        placementDTO.getImpressionTypeHandling(),
        context,
        getAnnotation());
  }

  public static boolean isValid(
      PublisherSiteDTO.SiteType siteType,
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      ImpressionTypeHandling impressionTypeHandling,
      ConstraintValidatorContext context,
      PlacementDTOImpressionTypeHandlingConstraint annotation) {
    if (!ValidationUtils.validateAllObjectsNotNull(
        context,
        annotation.field(),
        annotation.emptyMessage(),
        siteType,
        placementCategory,
        impressionTypeHandling)) {
      return false;
    }

    if (!isValid(siteType, placementCategory, videoSupport, impressionTypeHandling)) {
      ValidationUtils.addConstraintMessage(context, annotation.field(), annotation.message());
      return false;
    }

    return true;
  }

  public static boolean isValid(
      PublisherSiteDTO.SiteType siteType,
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      ImpressionTypeHandling impressionTypeHandling) {

    if (ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST.equals(impressionTypeHandling)) {
      return DapVideoPlacementUtil.isPlacementDapCompatible(
          placementCategory, videoSupport, siteType);
    }
    return true;
  }
}
