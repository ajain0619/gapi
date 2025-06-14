package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.BANNER;
import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.PlacementCategory.INTERSTITIAL;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.PlacementCategory.MEDIUM_RECTANGLE;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE_V2;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.util.validator.BaseValidator;
import com.nexage.app.util.validator.ValidationUtils;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PlacementDTOCategoryValidator
    extends BaseValidator<PlacementDTOCategoryConstraint, PlacementDTO> {

  private final Set<PlacementCategory> allowedApplicationPlacementCategories =
      EnumSet.of(
          BANNER, INTERSTITIAL, MEDIUM_RECTANGLE, NATIVE_V2, INSTREAM_VIDEO, IN_ARTICLE, IN_FEED);
  private final Set<PlacementCategory> allowedDesktopOrMobileWebOrWebsitePlacementCategories =
      EnumSet.of(BANNER, INTERSTITIAL, MEDIUM_RECTANGLE, INSTREAM_VIDEO, IN_ARTICLE, IN_FEED);
  private final Set<PlacementCategory> allowedDoohPlacementCategories =
      EnumSet.of(BANNER, INTERSTITIAL, INSTREAM_VIDEO);

  @Override
  public boolean isValid(PlacementDTO placementDTO, ConstraintValidatorContext context) {

    PlacementCategory placementCategory = placementDTO.getPlacementCategory();
    Type siteType = Optional.ofNullable(placementDTO.getSite()).map(SiteDTO::getType).orElse(null);
    Platform platformType =
        Optional.ofNullable(placementDTO.getSite()).map(SiteDTO::getPlatform).orElse(null);

    if (!ValidationUtils.validateAllObjectsNotNull(
        context,
        getAnnotation().field(),
        getAnnotation().emptyMessage(),
        placementCategory,
        siteType,
        platformType)) {
      return false;
    }

    boolean result = isPlacementCategoryValid(placementCategory, siteType, platformType);
    if (!result) {
      addConstraintMessage(context, getAnnotation().field(), getAnnotation().message());
    }
    return result;
  }

  // Platform type will be used when CTV_OTT is added in next PR
  public boolean isPlacementCategoryValid(
      PlacementCategory placementCategory, Type siteType, Platform platformType) {
    boolean result;
    switch (siteType) {
      case APPLICATION:
        result = allowedApplicationPlacementCategories.contains(placementCategory);
        if (Platform.CTV_OTT == platformType && INSTREAM_VIDEO != placementCategory) {
          result = false;
        }
        break;
      case DESKTOP:
      case MOBILE_WEB:
      case WEBSITE:
        result = allowedDesktopOrMobileWebOrWebsitePlacementCategories.contains(placementCategory);
        break;
      case DOOH:
        result = allowedDoohPlacementCategories.contains(placementCategory);
        break;
      default:
        result = false;
        break;
    }
    return result;
  }
}
