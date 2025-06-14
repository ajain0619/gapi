package com.nexage.app.util.placement;

import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.app.dto.publisher.PublisherSiteDTO;

public final class DapVideoPlacementUtil {

  private DapVideoPlacementUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static final String PLAYER_ID_AND_PLAYLIST_ID_PATTERN = "^[A-Za-z0-9-]+$";
  public static final String DEFAULT_O2_PLAYER_ID = "5bb26ab96614d13012e1fbc8";
  public static final String DEFAULT_O2_PLAYLIST_ID = "5bb7a2fe1b8535000179657b";
  public static final String DEFAULT_YAHOO_PLAYLIST_ID = "818fdeb0-9885-11e5-afe5-6b9d1fd155d3";
  public static final Integer MAX_PLAYER_ID_AND_PLAYLIST_ID_LENGTH = 60;

  public static boolean isPlacementDapCompatible(
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      PublisherSiteDTO.SiteType siteType) {

    return (placementCategory == PlacementCategory.BANNER
            || placementCategory == PlacementCategory.INTERSTITIAL
            || placementCategory == PlacementCategory.MEDIUM_RECTANGLE)
        && (siteType == PublisherSiteDTO.SiteType.MOBILE_WEB
            || siteType == PublisherSiteDTO.SiteType.DESKTOP
            || siteType == PublisherSiteDTO.SiteType.WEBSITE)
        && (videoSupport == null || videoSupport == VIDEO_AND_BANNER);
  }
}
