package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.BANNER;
import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.PlacementCategory.INTERSTITIAL;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.PlacementCategory.MEDIUM_RECTANGLE;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE_V2;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;

import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementDTOAdSizeValidatorTest extends BaseValidatorTest {

  @Mock private PlacementDTOAdSizeConstraint placementDTOAdSizeConstraint;
  @InjectMocks private PlacementDTOAdSizeValidator validator;

  @Test
  void validMediumRectangleEmptyAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(MEDIUM_RECTANGLE);
    placement.setAdSizeType(AdSizeType.CUSTOM);
    placement.setWidth(null);
    placement.setHeight(null);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validNativeV2EmptyAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(NATIVE_V2);
    placement.setWidth(null);
    placement.setHeight(null);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validMediumRectangleAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(MEDIUM_RECTANGLE);
    placement.setAdSizeType(AdSizeType.CUSTOM);
    placement.setWidth(1000);
    placement.setHeight(1000);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validBannerAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(BANNER);
    placement.setAdSizeType(AdSizeType.STANDARD);
    placement.setWidth(300);
    placement.setHeight(50);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validInterstitialAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(INTERSTITIAL);
    placement.setAdSizeType(AdSizeType.STANDARD);
    placement.setWidth(300);
    placement.setHeight(250);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validInStreamVideoAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(INSTREAM_VIDEO);
    placement.setAdSizeType(AdSizeType.STANDARD);
    placement.setWidth(1920);
    placement.setHeight(1080);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void invalidEmptyAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(BANNER);
    placement.setAdSizeType(AdSizeType.CUSTOM);
    placement.setWidth(null);
    placement.setHeight(null);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidBannerAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(BANNER);
    placement.setAdSizeType(AdSizeType.STANDARD);
    placement.setWidth(1000);
    placement.setHeight(1000);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidInStreamVideoAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(INSTREAM_VIDEO);
    placement.setAdSizeType(AdSizeType.STANDARD);
    placement.setWidth(1000);
    placement.setHeight(1000);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidInterstitialAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(INTERSTITIAL);
    placement.setAdSizeType(AdSizeType.STANDARD);
    placement.setWidth(1000);
    placement.setHeight(1000);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidNullPlacementCategory() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(null);
    placement.setAdSizeType(AdSizeType.DYNAMIC);
    placement.setWidth(1000);
    placement.setHeight(1000);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidNullAdSizeType() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(BANNER);
    placement.setAdSizeType(null);
    placement.setWidth(1000);
    placement.setHeight(1000);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidNullAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(BANNER);
    placement.setAdSizeType(AdSizeType.DYNAMIC);
    placement.setWidth(null);
    placement.setHeight(1000);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidAdSizeNotSet() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(BANNER);
    placement.setAdSizeType(AdSizeType.DYNAMIC);
    boolean valid = validator.isValid(placement, ctx);
    assertFalse(valid);
  }

  @Test
  void validInArticleEmptyAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(IN_ARTICLE);
    placement.setWidth(null);
    placement.setHeight(null);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validInFeedEmptyAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(IN_FEED);
    placement.setWidth(null);
    placement.setHeight(null);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validInArticleNonEmptyAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(IN_ARTICLE);
    placement.setWidth(100);
    placement.setHeight(100);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validInFeedNonEmptyAdSize() {
    PlacementDTO placement = new PlacementDTO();
    placement.setPlacementCategory(IN_FEED);
    placement.setWidth(100);
    placement.setHeight(100);
    boolean valid = validator.isValid(placement, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Override
  public void initializeConstraint() {
    lenient()
        .when(placementDTOAdSizeConstraint.message())
        .thenReturn("Placement AdSize is invalid for placementCategory");
    lenient()
        .when(placementDTOAdSizeConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
  }
}
