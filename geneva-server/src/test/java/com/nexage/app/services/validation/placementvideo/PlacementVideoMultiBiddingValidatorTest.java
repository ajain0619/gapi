package com.nexage.app.services.validation.placementvideo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.util.validator.placement.PlacementVideoMultiBiddingConstraint;
import com.nexage.app.util.validator.placement.PlacementVideoMultiBiddingValidator;
import com.nexage.app.web.support.TestObjectsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementVideoMultiBiddingValidatorTest extends BaseValidatorTest {
  @Mock private PlacementVideoMultiBiddingConstraint annotation;

  @InjectMocks private PlacementVideoMultiBiddingValidator validator;

  @BeforeEach
  public void setup() throws Exception {
    initializeContext();
    initializeConstraint();
  }

  private void verifyValidationMessage(String errorMessage) {
    verify(ctx, atLeastOnce()).buildConstraintViolationWithTemplate(errorMessage);
  }

  @Test
  void shouldNotThrowErrorWhenMultiImpressionBidIsFalseAndLongformIsDisabled() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(false);
    placementVideoDTO.setLongform(false);
    assertTrue(validator.isValid(placementVideoDTO, ctx));
  }

  @Test
  void shouldNotThrowErrorWhenMultiImpressionBidIsFalseAndLongformIsEnabled() {
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(false);
    assertTrue(validator.isValid(placementVideoDTO, ctx));
  }

  @Test
  void shouldNotThrowErrorWhenMultiImpressionBidIsTrueAndLongformIsEnabled() {
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(true);
    assertTrue(validator.isValid(placementVideoDTO, ctx));
  }

  @Test
  void shouldThrowErrorWhenMultiImpressionBidIsTrueButLongformIsDisabled() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(true);
    placementVideoDTO.setLongform(false);
    assertFalse(validator.isValid(placementVideoDTO, ctx));
    verifyValidationMessage(ServerErrorCodes.SERVER_INVALID_VIDEO_MULTI_IMPRESSION_BID.toString());
  }

  @Test
  void shouldNotThrowErrorWhenCompetitiveSeparationIsFalseAndMultiImpressionBidIsDisabled() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(false);
    placementVideoDTO.setCompetitiveSeparation(false);
    assertTrue(validator.isValid(placementVideoDTO, ctx));
  }

  @Test
  void shouldNotThrowErrorWhenCompetitiveSeparationIsFalseAndMultiImpressionBidIsEnabled() {
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(true);
    placementVideoDTO.setCompetitiveSeparation(false);
    assertTrue(validator.isValid(placementVideoDTO, ctx));
  }

  @Test
  void shouldNotThrowErrorWhenCompetitiveSeparationIsTrueAndMultiImpressionBidIsEnabled() {
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(true);
    placementVideoDTO.setCompetitiveSeparation(true);
    assertTrue(validator.isValid(placementVideoDTO, ctx));
  }

  @Test
  void shouldThrowErrorWhenCompetitiveSeparationIsTrueButMultiImpressionBidIsDisabled() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(false);
    placementVideoDTO.setCompetitiveSeparation(true);
    assertFalse(validator.isValid(placementVideoDTO, ctx));
    verifyValidationMessage(
        ServerErrorCodes.SERVER_INVALID_VIDEO_COMPETITIVE_SEPARATION.toString());
  }

  @Override
  public void initializeConstraint() {
    lenient().when(annotation.message()).thenReturn(ValidationMessages.WRONG_VALUE);
  }
}
