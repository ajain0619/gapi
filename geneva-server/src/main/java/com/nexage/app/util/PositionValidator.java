package com.nexage.app.util;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.placement.PlacementDTOImpressionTypeHandlingValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.StaleStateException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component
public class PositionValidator {

  private static final String VALID_PLAYBACK_METHOD_VALUES = "1,2,3,4";
  private static final int POSITION_ALIAS_NAME_LENGTH = 45;

  private final PositionRepository positionRepository;

  private static final int MAX_NAME_LENGTH = 45;

  /**
   * verify position version is legal
   *
   * @param position
   * @param publisherPositionVersion
   */
  public void validateVersion(Position position, Integer publisherPositionVersion) {
    if (!Objects.equals(position.getVersion(), publisherPositionVersion)) {
      // throw stale data exception
      throw new StaleStateException("PublisherPosition has a different version of the data");
    }
  }

  /**
   * Verify a position name is present when updating position
   *
   * @param position
   */
  public void validateName(PublisherPositionDTO position) {
    if (StringUtils.isBlank(position.getName()) || position.getName().length() > MAX_NAME_LENGTH) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_POSITION_NAME_LENGTH);
    }
  }

  public void validatePublisherPositionAliasName(PublisherPositionDTO position) {
    final String positionAliasName = position.getPositionAliasName();
    if (positionAliasName != null) {
      if (positionAliasName.length() > POSITION_ALIAS_NAME_LENGTH) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_POSITION_ALIAS_NAME_LENGTH_EXCEEDED);
      }

      List<Position> positionsByAliasName =
          positionRepository.findByPositionAliasName(positionAliasName);
      if (CollectionUtils.isNotEmpty(positionsByAliasName)
          && !positionsByAliasName.get(0).getPid().equals(position.getPid())) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_POSITION_ALIAS_NAME);
      }
    }
  }

  public void validatePosition(Position position) {
    if (position.getPid() == null && position.getVersion() == null) {
      PlacementCategory pc = position.getPlacementCategory();
      validatePositionMraidAdvancedTracking(pc, position);
    }
    populateVideoLinearity(position);

    if (null != position.getHeight() && position.getHeight() < 1) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_HEIGHT);
    }

    if (null != position.getWidth() && position.getWidth() < 1) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_WIDTH);
    }

    validatePositionAliasName(position);

    if (position.getVideoSupport() == null
        || position.getVideoSupport() == VideoSupport.VIDEO
        || position.getVideoSupport() == VideoSupport.VIDEO_AND_BANNER) {
      // Validate optional video attributes
      String videoPlaybackMethods = position.getVideoPlaybackMethod();
      validatePositionVideoAttributes(videoPlaybackMethods, position);
    } else {
      // No video support, thus should not be any video attributes
      validatePositionNoVideoAttributes(position);
    }

    validateImpressionTypeHandling(position);
  }

  /*
   * Run several validations for position 1. See that the native version is in
   * the valid set of native versions 2. The max duration of video is at least
   * 1 3. If the video support is not video, all of the video-attributes
   * should be null
   */

  private void populateVideoLinearity(Position position) {
    if (null != position.getVideoSupport()
        && (position.getVideoSupport().equals(VideoSupport.VIDEO)
            || position.getVideoSupport().equals(VideoSupport.VIDEO_AND_BANNER))
        && position.getVideoLinearity() == null) {
      position.setVideoLinearity(VideoLinearity.LINEAR);
    }
  }

  private void validatePositionMraidAdvancedTracking(PlacementCategory pc, Position position) {
    if (pc.equals(PlacementCategory.BANNER) || pc.equals(PlacementCategory.MEDIUM_RECTANGLE)) {
      if (position.isMraidAdvancedTracking()) {
        log.warn(
            "mraidAdvancedTracking is not allowed to be enabled for this placement category when creating new position for site : {} ",
            position.getSitePid());
      }
      position.setMraidAdvancedTracking(false);
    }
  }

  private void validatePositionVideoAttributes(String videoPlaybackMethods, Position position) {
    if (null != videoPlaybackMethods) {
      String[] playbackMethods = videoPlaybackMethods.split(",");
      for (String s : playbackMethods) {
        if (!VALID_PLAYBACK_METHOD_VALUES.contains(s))
          throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_PLAYBACK_METHOD);
      }
    }

    if (null != position.getVideoMaxdur() && position.getVideoMaxdur() <= 0) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_VIDEO_MAXDUR);
    }

    if (null != position.getVideoStartDelay() && position.getVideoStartDelay() < -2) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_VIDEO_STARTDELAY);
    }

    if (null != position.getVideoSkipThreshold() && position.getVideoSkipThreshold() < 0) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_VIDEO_SKIPTHRESHOLD);
    }

    if (null != position.getVideoSkipOffset() && position.getVideoSkipOffset() < 0) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_VIDEO_SKIPOFFSET);
    }
  }

  private void validatePositionNoVideoAttributes(Position position) {
    if (position.getVideoLinearity() != null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_UNSUPPORTED_VIDEO_ATTRIBUTES);
    }
  }

  private void validatePositionAliasName(Position position) {
    final String positionAliasName = position.getPositionAliasName();
    if (positionAliasName != null) {
      if (positionAliasName.length() > POSITION_ALIAS_NAME_LENGTH) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_POSITION_ALIAS_NAME_LENGTH_EXCEEDED);
      }

      List<Position> positionsByAliasName =
          positionRepository.findByPositionAliasName(positionAliasName);
      if (CollectionUtils.isNotEmpty(positionsByAliasName)
          && !positionsByAliasName.get(0).getPid().equals(position.getPid())) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_POSITION_ALIAS_NAME);
      }
    }
  }

  private void validateImpressionTypeHandling(Position position) {
    PublisherSiteDTO.SiteType pubSiteType =
        position.getSite() != null
            ? PublisherSiteDTO.SiteType.valueOf(position.getSite().getType().toString())
            : null;
    if (!PlacementDTOImpressionTypeHandlingValidator.isValid(
        pubSiteType,
        position.getPlacementCategory(),
        position.getVideoSupport(),
        position.getImpressionTypeHandling())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_IMPRESSION_TYPE_HANDLING);
    }
  }
}
