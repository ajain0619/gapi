package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.sparta.jpa.model.TagRule;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BuyerService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagValidatorTest {
  @Mock private BuyerService buyerService;

  @InjectMocks TagValidator tagValidator;

  private Tag tag = new Tag();

  @Test
  void testTagValidation() {
    tagValidator.validateTag(tag);
  }

  @Test
  void testTagInvalidHeight() {
    tag.setHeight(-10);

    assertThrows(
        GenevaValidationException.class,
        () -> tagValidator.validateTag(tag),
        ServerErrorCodes.SERVER_INVALID_HEIGHT.toString());
  }

  @Test
  void testTagInvalidWidth() {
    tag.setWidth(-10);

    assertThrows(
        GenevaValidationException.class,
        () -> tagValidator.validateTag(tag),
        ServerErrorCodes.SERVER_INVALID_WIDTH.toString());
  }

  @Test
  void testTagInvalidVideoAttributes() {
    tag.setVideoSupport(VideoSupport.VIDEO);
    tag.setVideoPlaybackMethod("7");

    assertThrows(
        GenevaValidationException.class,
        () -> tagValidator.validateTag(tag),
        ServerErrorCodes.SERVER_INVALID_PLAYBACK_METHOD.toString());

    tag.setVideoPlaybackMethod(null);
    tag.setVideoMaxdur(0);

    assertThrows(
        GenevaValidationException.class,
        () -> tagValidator.validateTag(tag),
        ServerErrorCodes.SERVER_INVALID_VIDEO_MAXDUR.toString());

    tag.setVideoMaxdur(null);
    tag.setVideoStartDelay(-10);

    assertThrows(
        GenevaValidationException.class,
        () -> tagValidator.validateTag(tag),
        ServerErrorCodes.SERVER_INVALID_VIDEO_STARTDELAY.toString());

    tag.setVideoStartDelay(null);
    tag.setVideoSkipThreshold(-10);

    assertThrows(
        GenevaValidationException.class,
        () -> tagValidator.validateTag(tag),
        ServerErrorCodes.SERVER_INVALID_VIDEO_SKIPTHRESHOLD.toString());

    tag.setVideoSkipThreshold(null);
    tag.setVideoSkipOffset(-10);

    assertThrows(
        GenevaValidationException.class,
        () -> tagValidator.validateTag(tag),
        ServerErrorCodes.SERVER_INVALID_VIDEO_SKIPOFFSET.toString());
  }

  @Test
  void testTagUnsupportedVideoAttributesThrowsException() {
    tag.setVideoSupport(VideoSupport.BANNER);
    tag.setVideoLinearity(VideoLinearity.LINEAR);

    assertThrows(
        GenevaValidationException.class,
        () -> tagValidator.validateTag(tag),
        ServerErrorCodes.SERVER_UNSUPPORTED_VIDEO_ATTRIBUTES.toString());
  }

  @Test
  void shouldThrowExceptionOnUnsupportedVideSupport() {
    tag.setVideoSupport(VideoSupport.NATIVE);
    var exception =
        assertThrows(GenevaValidationException.class, () -> tagValidator.validateTag(tag));
    assertEquals(
        ServerErrorCodes.SERVER_UNSUPPORTED_VIDEO_SUPPORT_OVERRIDE, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenAdSourceDoesNotAllowSmartYield() {
    tag.setVideoSupport(VideoSupport.NATIVE);
    TagRule rule = new TagRule();
    rule.setRuleType(TagRule.RuleType.SmartYield);
    tag.setRules(Set.of(rule));
    AdSource adSource = new AdSource();
    adSource.setUseWrappedSdk(false);
    when(buyerService.getAdSource(any())).thenReturn(adSource);
    var exception =
        assertThrows(GenevaValidationException.class, () -> tagValidator.validateTag(tag));
    assertEquals(
        ServerErrorCodes.SERVER_SMARTYIELD_NOT_ALLOWED_FOR_ADSOURCE, exception.getErrorCode());
  }
}
