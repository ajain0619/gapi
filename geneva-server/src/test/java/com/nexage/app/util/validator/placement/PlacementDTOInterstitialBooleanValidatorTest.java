package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.BANNER;
import static com.nexage.admin.core.enums.PlacementCategory.INTERSTITIAL;
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

class PlacementDTOInterstitialBooleanValidatorTest extends BaseValidatorTest {

  @Mock private PlacementDTOInterstitialBooleanConstraint placementDTOInterstitialBooleanConstraint;
  @InjectMocks private PlacementDTOInterstitialBooleanValidator validator;

  @Test
  void validWhenInterstitialTrueAndCategoryInterstitial() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setInterstitial(true);
    placementDTO.setPlacementCategory(INTERSTITIAL);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validWhenInterstitialFalseAndCategoryNotInterstitial() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setInterstitial(false);
    placementDTO.setPlacementCategory(BANNER);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void invalidWhenInterstitialFalseAndCategoryInterstitial() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setInterstitial(false);
    placementDTO.setPlacementCategory(INTERSTITIAL);
    boolean valid = validator.isValid(placementDTO, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidWhenInterstitialNullAndCategoryInterstitial() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setInterstitial(null);
    placementDTO.setPlacementCategory(INTERSTITIAL);
    boolean valid = validator.isValid(placementDTO, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidWhenInterstitialTrueAndCategoryNull() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setInterstitial(true);
    placementDTO.setPlacementCategory(null);
    boolean valid = validator.isValid(placementDTO, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidWhenInterstitialTrueAndCategoryNotInterstitial() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setInterstitial(true);
    placementDTO.setPlacementCategory(BANNER);
    boolean valid = validator.isValid(placementDTO, ctx);
    assertFalse(valid);
  }

  @Override
  public void initializeConstraint() {
    lenient()
        .when(placementDTOInterstitialBooleanConstraint.message())
        .thenReturn("Interstitial boolean invalid");
    lenient()
        .when(placementDTOInterstitialBooleanConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
    lenient().when(placementDTOInterstitialBooleanConstraint.field()).thenReturn("interstitial");
  }
}
