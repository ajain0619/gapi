package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.BANNER;
import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.PlacementCategory.INTERSTITIAL;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.web.support.TestObjectsFactory;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementDTOVideoSettingsValidatorTest extends BaseValidatorTest {

  @Mock private PlacementDTOVideoSettingsConstraint placementDTOVideoSettingsConstraint;
  @InjectMocks private PlacementDTOVideoSettingsValidator validator;

  @Test
  void trueWhenPlacementDTOVideo() {
    PlacementDTO placementDTO = createPlacementDTO(VideoSupport.VIDEO);
    assertTrue(validator.isValid(placementDTO, ctx));
    verify(ctx, never()).buildConstraintViolationWithTemplate(anyString());
  }

  @Test
  void trueWhenPlacementDTOVideoAndBanner() {
    PlacementDTO placementDTO = createPlacementDTO(VideoSupport.VIDEO_AND_BANNER);
    assertTrue(validator.isValid(placementDTO, ctx));
    verify(ctx, never()).buildConstraintViolationWithTemplate(anyString());
  }

  @Test
  void falseWhenPlacementCategoryNull() {
    PlacementDTO placementDTO = createPlacementDTO(VideoSupport.VIDEO);
    placementDTO.setPlacementCategory(null);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(ValidationMessages.WRONG_IS_EMPTY);
  }

  @Test
  void trueWhenVideoSupportIsNull() {
    PlacementDTO placementDTO = createPlacementDTO(VideoSupport.VIDEO);
    placementDTO.setVideoSupport(null);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  @Test
  void trueWhenBannerAndAllVideoIsNull() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setPlacementCategory(BANNER);
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    assertTrue(validator.isValid(placementDTO, ctx));
    verify(ctx, never()).buildConstraintViolationWithTemplate(anyString());
  }

  @Test
  void falseWhenNonInStreamVideoAndPlacementVideoLongformTrue() {
    PlacementDTO placementDTO = createPlacementDTO(VideoSupport.VIDEO);
    placementDTO.setPlacementCategory(INTERSTITIAL);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLongform(true);
    placementDTO.setPlacementVideo(placementVideoDTO);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(
        placementDTOVideoSettingsConstraint.invalidPlacementCategoryForLongform());
  }

  @Test
  void trueWhenNonInStreamVideoAndPlacementVideoLongformTrue() {
    PlacementDTO placementDTO = createPlacementDTO(VideoSupport.VIDEO);
    placementDTO.setPlacementCategory(INSTREAM_VIDEO);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLongform(true);
    placementDTO.setPlacementVideo(placementVideoDTO);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  @Test
  void trueWhenPlacementVideoDTOIsPresentAndPlacementDTODoesNotHaveVideoParams() {
    PlacementDTO placementDTO = createPlacementDTO(VideoSupport.VIDEO);
    placementDTO.setPlacementCategory(INSTREAM_VIDEO);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementDTO.setPlacementVideo(placementVideoDTO);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  private void verifyValidationMessage(String expectedMessage) {
    verify(ctx).buildConstraintViolationWithTemplate(expectedMessage);
  }

  private PlacementDTO createPlacementDTO(VideoSupport videoSupport) {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setPlacementCategory(INSTREAM_VIDEO);
    placementDTO.setVideoSupport(videoSupport);
    PlacementVideoDTO placementVideoDTO = new PlacementVideoDTO();
    placementDTO.setPlacementVideo(placementVideoDTO);
    return placementDTO;
  }

  @Override
  public void initializeConstraint() {
    lenient()
        .when(placementDTOVideoSettingsConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
    lenient()
        .when(placementDTOVideoSettingsConstraint.invalidPlacementCategoryForLongform())
        .thenReturn("Placement category must be INSTREAM_VIDEO for enabling longform");
  }
}
