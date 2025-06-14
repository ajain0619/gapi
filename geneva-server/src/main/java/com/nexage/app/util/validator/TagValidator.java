package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.sparta.jpa.model.TagRule;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BuyerService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TagValidator {
  private static final String VALID_PLAYBACK_METHOD_VALUES = "1,2,3,4";

  private final BuyerService buyerService;

  public void validateTag(Tag tag) {
    if (null != tag.getHeight() && tag.getHeight() < 1) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_HEIGHT);
    }

    if (null != tag.getWidth() && tag.getWidth() < 1) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_WIDTH);
    }
    if (null != tag.getRules()) {
      validateTagRules(tag);
    }
    if (tag.getVideoSupport() == null
        || tag.getVideoSupport() == VideoSupport.VIDEO
        || tag.getVideoSupport() == VideoSupport.VIDEO_AND_BANNER) {
      // Validate optional video attributes
      String videoPlaybackMethods = tag.getVideoPlaybackMethod();
      validateTagVideoAttributes(videoPlaybackMethods, tag);
    } else if (tag.getVideoSupport() == VideoSupport.NATIVE) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_UNSUPPORTED_VIDEO_SUPPORT_OVERRIDE);
    } else {
      // Banner
      // No video support, thus should not be any video attributes
      validateTagNoVideoAttributes(tag);
    }
  }

  private void validateTagRules(Tag tag) {
    for (TagRule rule : tag.getRules()) {
      if (rule.getRuleType().equals(TagRule.RuleType.SmartYield)) {
        AdSource adSource = buyerService.getAdSource(tag.getBuyerPid());
        if (!adSource.isUseWrappedSdk())
          throw new GenevaValidationException(
              ServerErrorCodes.SERVER_SMARTYIELD_NOT_ALLOWED_FOR_ADSOURCE);
      }
    }
  }

  private void validateTagVideoAttributes(String videoPlaybackMethods, Tag tag) {
    if (null != videoPlaybackMethods) {
      String[] playbackMethods = videoPlaybackMethods.split(",");
      for (String s : playbackMethods) {
        if (!VALID_PLAYBACK_METHOD_VALUES.contains(s))
          throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_PLAYBACK_METHOD);
      }
    }

    if (null != tag.getVideoMaxdur() && tag.getVideoMaxdur() <= 0) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_VIDEO_MAXDUR);
    }

    if (null != tag.getVideoStartDelay() && tag.getVideoStartDelay() < -2) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_VIDEO_STARTDELAY);
    }

    if (null != tag.getVideoSkipThreshold() && tag.getVideoSkipThreshold() < 0) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_VIDEO_SKIPTHRESHOLD);
    }

    if (null != tag.getVideoSkipOffset() && tag.getVideoSkipOffset() < 0) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_VIDEO_SKIPOFFSET);
    }
  }

  private void validateTagNoVideoAttributes(Tag tag) {
    if (tag.getVideoLinearity() != null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_UNSUPPORTED_VIDEO_ATTRIBUTES);
    }
  }
}
