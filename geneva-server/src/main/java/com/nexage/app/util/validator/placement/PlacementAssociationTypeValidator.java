package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.AssociationType.DEFAULT;
import static com.nexage.admin.core.enums.AssociationType.DEFAULT_BANNER;
import static com.nexage.admin.core.enums.AssociationType.DEFAULT_VIDEO;
import static com.nexage.admin.core.enums.AssociationType.NON_DEFAULT;
import static com.nexage.admin.core.enums.PlacementCategory.BANNER;
import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.PlacementCategory.INTERSTITIAL;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.PlacementCategory.MEDIUM_RECTANGLE;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE_V2;
import static com.nexage.admin.core.enums.PlacementCategory.REWARDED_VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;

import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class PlacementAssociationTypeValidator {

  private PlacementAssociationTypeValidator() {}

  private static final Set<PlacementCategory> videoAndBannerDefaultsSupportedPlacementCategories =
      EnumSet.of(BANNER, INTERSTITIAL, NATIVE, NATIVE_V2, MEDIUM_RECTANGLE);
  private static final Set<PlacementCategory> videoDefaultSupportedPlacementCategories =
      EnumSet.of(INSTREAM_VIDEO, IN_ARTICLE, IN_FEED, REWARDED_VIDEO);
  private static final Set<VideoSupport> videoAndBannerDefaultsSupportedVideoSupport =
      EnumSet.of(VIDEO_AND_BANNER, VideoSupport.NATIVE);

  public static boolean isAssociationTypeValid(
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet,
      List<Long> formattedAssociationTypeSupportedHBPartners) {
    if (CollectionUtils.isEmpty(hbPartnerAssignmentDTOSet)) {
      return true;
    }
    return hbPartnerAssignmentDTOSet.stream()
        .allMatch(
            hbPartnerAssignmentDTO ->
                (NON_DEFAULT.equals(hbPartnerAssignmentDTO.getType())
                    || isValidDefaultValue(
                        hbPartnerAssignmentDTO,
                        placementCategory,
                        videoSupport,
                        formattedAssociationTypeSupportedHBPartners)));
  }

  private static boolean isValidDefaultValue(
      HbPartnerAssignmentDTO hbPartnerAssignmentDTO,
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      List<Long> formattedAssociationTypeSupportedHBPartners) {
    if (formattedAssociationTypeSupportedHBPartners.contains(
        hbPartnerAssignmentDTO.getHbPartnerPid())) {
      if (videoAndBannerDefaultsSupportedPlacementCategories.contains(placementCategory)) {
        if (isAssociationTypeValidForVideoSupport(videoSupport, hbPartnerAssignmentDTO.getType())) {
          return true;
        }
      } else if (videoDefaultSupportedPlacementCategories.contains(placementCategory)
          && DEFAULT_VIDEO.equals(hbPartnerAssignmentDTO.getType())) {
        return true;
      }
    } else {
      if (DEFAULT.equals(hbPartnerAssignmentDTO.getType())) {
        return true;
      }
    }
    return false;
  }

  private static boolean isAssociationTypeValidForVideoSupport(
      VideoSupport videoSupport, AssociationType associationType) {
    return videoSupport == null
        || (VideoSupport.BANNER.equals(videoSupport) && DEFAULT_BANNER.equals(associationType)
            || VIDEO.equals(videoSupport) && DEFAULT_VIDEO.equals(associationType)
            || (videoAndBannerDefaultsSupportedVideoSupport.contains(videoSupport)
                && (DEFAULT_BANNER.equals(associationType)
                    || DEFAULT_VIDEO.equals(associationType))));
  }
}
