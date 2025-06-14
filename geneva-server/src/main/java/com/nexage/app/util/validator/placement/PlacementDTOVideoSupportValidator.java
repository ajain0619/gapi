package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.VideoSupport.BANNER;
import static com.nexage.admin.core.enums.VideoSupport.NATIVE;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;
import static com.nexage.admin.core.enums.site.Type.APPLICATION;
import static com.nexage.admin.core.enums.site.Type.DESKTOP;
import static com.nexage.admin.core.enums.site.Type.DOOH;
import static com.nexage.admin.core.enums.site.Type.MOBILE_WEB;
import static com.nexage.admin.core.enums.site.Type.WEBSITE;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
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
public class PlacementDTOVideoSupportValidator
    extends BaseValidator<PlacementDTOVideoSupportConstraint, PlacementDTO> {

  private final Set<VideoSupport> VIDEO_SUPPORT_VIDEO_TYPES =
      EnumSet.of(VIDEO, VIDEO_AND_BANNER, BANNER);
  private final Set<Type> INSTREAM_VIDEO_ALLOWED_SITE_TYPES =
      EnumSet.of(DESKTOP, APPLICATION, MOBILE_WEB, DOOH, WEBSITE);

  @Override
  public boolean isValid(PlacementDTO placementDTO, ConstraintValidatorContext context) {

    Type siteType = Optional.ofNullable(placementDTO.getSite()).map(SiteDTO::getType).orElse(null);
    Platform platformType =
        Optional.ofNullable(placementDTO.getSite()).map(SiteDTO::getPlatform).orElse(null);
    PlacementCategory placementCategory = placementDTO.getPlacementCategory();
    VideoSupport videoSupport = placementDTO.getVideoSupport();

    if (!ValidationUtils.validateAllObjectsNotNull(
        context,
        getAnnotation().field(),
        getAnnotation().emptyMessage(),
        placementCategory,
        siteType,
        platformType)) {
      return false;
    }
    boolean result = isVideoSupportValid(placementCategory, videoSupport, siteType, platformType);
    if (!result) {
      ValidationUtils.addConstraintMessage(
          context, getAnnotation().field(), getAnnotation().message());
    }
    return result;
  }

  // Platform type will be used when CTV_OTT is added in next PR
  public boolean isVideoSupportValid(
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      Type siteType,
      Platform platformType) {
    boolean result = false;

    if (videoSupport == null) {
      return true;
    }

    if (APPLICATION == siteType && Platform.CTV_OTT == platformType) {
      return (PlacementCategory.INSTREAM_VIDEO == placementCategory && VIDEO == videoSupport);
    }
    switch (placementCategory) {
      case BANNER:
        result =
            (BANNER == videoSupport)
                || (DOOH != siteType
                    && (VIDEO == videoSupport || VIDEO_AND_BANNER == videoSupport));
        break;
      case INTERSTITIAL:
        result = VIDEO_SUPPORT_VIDEO_TYPES.contains(videoSupport);
        break;
      case MEDIUM_RECTANGLE:
        result = DOOH != siteType && VIDEO_SUPPORT_VIDEO_TYPES.contains(videoSupport);
        break;
      case NATIVE_V2:
        result = NATIVE == videoSupport && APPLICATION == siteType;
        break;
      case INSTREAM_VIDEO:
        result = INSTREAM_VIDEO_ALLOWED_SITE_TYPES.contains(siteType) && VIDEO == videoSupport;
        break;
      case IN_ARTICLE:
      case IN_FEED:
        result = DOOH != siteType && VIDEO == videoSupport;
        break;
    }
    return result;
  }
}
