package com.nexage.app.util.validator.placement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;

import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementDTOAdSizeTypeValidatorTest extends BaseValidatorTest {

  @Mock private PlacementDTOAdSizeTypeConstraint placementDTOAdSizeTypeConstraint;
  @InjectMocks private PlacementDTOAdSizeTypeValidator validator;

  @Test
  void validDynamicBanner() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(PlacementCategory.BANNER);
    placement.setAdSizeType(AdSizeType.DYNAMIC);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validStandardVideo() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    placement.setAdSizeType(AdSizeType.STANDARD);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validCustomMediumRectangle() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(PlacementCategory.MEDIUM_RECTANGLE);
    placement.setAdSizeType(AdSizeType.CUSTOM);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validNativeV2() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(PlacementCategory.NATIVE_V2);
    placement.setAdSizeType(null);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void invalidVideoAdSizeType() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    placement.setAdSizeType(AdSizeType.DYNAMIC);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidNativeAdSizeType() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(PlacementCategory.NATIVE);
    placement.setAdSizeType(AdSizeType.DYNAMIC);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidNullAdSizeType() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(PlacementCategory.NATIVE);
    placement.setAdSizeType(null);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidNullPlacementCategory() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(null);
    placement.setAdSizeType(AdSizeType.DYNAMIC);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void validInArticlePlacementWhenTypeIsNull() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(PlacementCategory.IN_ARTICLE);
    placement.setAdSizeType(null);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validInFeedPlacementWhenTypeIsNull() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(PlacementCategory.IN_FEED);
    placement.setAdSizeType(null);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validInArticlePlacementWhenTypeIsNotNull() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(PlacementCategory.IN_ARTICLE);
    placement.setAdSizeType(AdSizeType.CUSTOM);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validInFeedPlacementWhenTypeIsNotNull() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(PlacementCategory.IN_FEED);
    placement.setAdSizeType(AdSizeType.CUSTOM);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Override
  public void initializeConstraint() {
    lenient()
        .when(placementDTOAdSizeTypeConstraint.message())
        .thenReturn("Placement AdSizeType is invalid for placementCategory");
    lenient()
        .when(placementDTOAdSizeTypeConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
  }
}
