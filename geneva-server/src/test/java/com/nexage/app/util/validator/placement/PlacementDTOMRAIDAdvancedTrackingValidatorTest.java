package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.BANNER;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE_V2;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;

import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementDTOMRAIDAdvancedTrackingValidatorTest extends BaseValidatorTest {

  @Mock
  private PlacementDTOMRAIDAdvancedTrackingConstraint placementDTOMRAIDAdvancedTrackingConstraint;

  @InjectMocks private PlacementDTOMRAIDAdvancedTrackingValidator validator;

  @Test
  void validNativeV2TrueMRAIDAdvanceTracking() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidAdvancedTracking(TRUE);
    placementDTO.setPlacementCategory(NATIVE_V2);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validNativeV2FalseMRAIDAdvanceTracking() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidAdvancedTracking(FALSE);
    placementDTO.setPlacementCategory(NATIVE_V2);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validBannerFalseMRAIDAdvanceTracking() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidAdvancedTracking(FALSE);
    placementDTO.setPlacementCategory(BANNER);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void invalidBannerTruePlacementCategory() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidAdvancedTracking(TRUE);
    placementDTO.setPlacementCategory(BANNER);
    boolean valid = validator.isValid(placementDTO, ctx);
    assertFalse(valid);
  }

  @Test
  void nullPlacementCategory() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidAdvancedTracking(TRUE);
    boolean valid = validator.isValid(placementDTO, ctx);
    assertFalse(valid);
  }

  @Test
  void validInArticleTrueMRAIDAdvanceTracking() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidAdvancedTracking(TRUE);
    placementDTO.setPlacementCategory(IN_ARTICLE);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validInArticleFalseMRAIDAdvanceTracking() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidAdvancedTracking(FALSE);
    placementDTO.setPlacementCategory(IN_ARTICLE);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validInFeedTrueMRAIDAdvanceTracking() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidAdvancedTracking(TRUE);
    placementDTO.setPlacementCategory(IN_FEED);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validInFeedFalseMRAIDAdvanceTracking() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidAdvancedTracking(FALSE);
    placementDTO.setPlacementCategory(IN_FEED);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Override
  public void initializeConstraint() {
    lenient()
        .when(placementDTOMRAIDAdvancedTrackingConstraint.message())
        .thenReturn("Invalid mraidAdvancedTracking value for PlacementCategory");
    lenient()
        .when(placementDTOMRAIDAdvancedTrackingConstraint.field())
        .thenReturn("mraidAdvancedTracking");
    lenient()
        .when(placementDTOMRAIDAdvancedTrackingConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
  }
}
