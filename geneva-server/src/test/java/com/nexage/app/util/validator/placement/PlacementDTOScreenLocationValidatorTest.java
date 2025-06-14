package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.BANNER;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.ScreenLocation.ABOVE_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.BELOW_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.FOOTER_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.FULLSCREEN_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.HEADER_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.SIDEBAR_VISIBLE;
import static com.nexage.admin.core.enums.ScreenLocation.UNDEFINED;
import static com.nexage.admin.core.enums.ScreenLocation.UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.ValidationMessages;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PlacementDTOScreenLocationValidatorTest {

  private final ScreenLocation[] staticTypeScreenLocationsButFullscreenVisible =
      new ScreenLocation[] {
        UNKNOWN, ABOVE_VISIBLE, BELOW_VISIBLE, HEADER_VISIBLE, FOOTER_VISIBLE, SIDEBAR_VISIBLE
      };

  private final ScreenLocation[] videoTypeScreenLocations =
      new ScreenLocation[] {FULLSCREEN_VISIBLE};

  private final ScreenLocation[] nativeTypeScreenLocations = new ScreenLocation[] {UNDEFINED};

  private Set<PlacementCategory> unknownPlacementCategories = ImmutableSet.of(IN_ARTICLE, IN_FEED);

  @Mock private ConstraintValidatorContext ctx;
  @Mock private PlacementDTOScreenLocationConstraint placementDTOScreenLocationConstraint;

  private PlacementDTOScreenLocationValidator validator = new PlacementDTOScreenLocationValidator();

  @BeforeEach
  public void setup() {
    initializeContext();
    initializeConstraint();
  }

  @Test
  void falseWhenPlacementCategoryNull() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setPlacementCategory(null);
    placementDTO.setScreenLocation(ABOVE_VISIBLE);
    assertFalse(validator.isValid(placementDTO, ctx));
    verifyValidationMessage(ValidationMessages.WRONG_IS_EMPTY);
  }

  @Test
  void trueWhenPlacementCategoryInstreamVideoAndVideoScreenLocation() {
    Stream.of(videoTypeScreenLocations)
        .forEach(
            screenLocation ->
                assertTrue(
                    validator.isValid(
                        createMinimalPlacementDTO(PlacementCategory.INSTREAM_VIDEO, screenLocation),
                        ctx),
                    "Expected true when placementCategory: INSTREAM_VIDEO and screenLocation: "
                        + screenLocation));
  }

  @Test
  void trueWhenPlacementCategoryInterstitialAndVideoScreenLocation() {
    Stream.of(videoTypeScreenLocations)
        .forEach(
            screenLocation ->
                assertTrue(
                    validator.isValid(
                        createMinimalPlacementDTO(PlacementCategory.INTERSTITIAL, screenLocation),
                        ctx),
                    "Expected true when placementCategory: INTERSTITIAL and screenLocation: "
                        + screenLocation));
  }

  @Test
  void trueWhenPlacementCategoryRewardedVideoAndVideoScreenLocation() {
    Stream.of(videoTypeScreenLocations)
        .forEach(
            screenLocation ->
                assertTrue(
                    validator.isValid(
                        createMinimalPlacementDTO(PlacementCategory.REWARDED_VIDEO, screenLocation),
                        ctx),
                    "Expected true when placementCategory: REWARDED_VIDEO and screenLocation: "
                        + screenLocation));
  }

  @Test
  void trueWhenPlacementCategoryBannerAndStaticScreenLocation() {
    Stream.of(staticTypeScreenLocationsButFullscreenVisible)
        .forEach(
            screenLocation ->
                assertTrue(
                    validator.isValid(createMinimalPlacementDTO(BANNER, screenLocation), ctx),
                    "Expected true when placementCategory: BANNER and screenLocation: "
                        + screenLocation));
  }

  @Test
  void trueWhenPlacementCategoryMediumRectangleAndStaticScreenLocation() {
    Stream.of(staticTypeScreenLocationsButFullscreenVisible)
        .forEach(
            screenLocation ->
                assertTrue(
                    validator.isValid(
                        createMinimalPlacementDTO(
                            PlacementCategory.MEDIUM_RECTANGLE, screenLocation),
                        ctx),
                    "Expected true when placementCategory: MEDIUM_RECTANGLE and screenLocation: "
                        + screenLocation));
  }

  @Test
  void falseWhenPlacementCategoryInstreamVideoAndNativeScreenLocation() {
    Stream.of(nativeTypeScreenLocations)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(
                        createMinimalPlacementDTO(PlacementCategory.INSTREAM_VIDEO, screenLocation),
                        ctx),
                    "Expected false when placementCategory: INSTREAM_VIDEO and screenLocation: "
                        + screenLocation));
  }

  @Test
  void falseWhenPlacementCategoryInterstitialAndNativeScreenLocation() {
    Stream.of(nativeTypeScreenLocations)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(
                        createMinimalPlacementDTO(PlacementCategory.INTERSTITIAL, screenLocation),
                        ctx),
                    "Expected false when placementCategory: INTERSTITIAL and screenLocation: "
                        + screenLocation));
  }

  @Test
  void trueWhenPlacementCategorySetAndScreenLocationNull() {
    Stream.of(PlacementCategory.values())
        .forEach(
            placementCategory ->
                assertTrue(
                    validator.isValid(createMinimalPlacementDTO(placementCategory, null), ctx),
                    "Expected false when screenLocation: null and placementCategory: "
                        + placementCategory));
  }

  @Test
  void falseWhenPlacementCategoryRewardedVideoAndNativeScreenLocation() {
    Stream.of(nativeTypeScreenLocations)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(
                        createMinimalPlacementDTO(PlacementCategory.REWARDED_VIDEO, screenLocation),
                        ctx),
                    "Expected false when placementCategory: REWARDED_VIDEO and screenLocation: "
                        + screenLocation));
  }

  @Test
  void falseWhenPlacementCategoryBannerAndNativeScreenLocation() {
    Stream.of(nativeTypeScreenLocations)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(createMinimalPlacementDTO(BANNER, screenLocation), ctx),
                    "Expected false when placementCategory: BANNER and screenLocation: "
                        + screenLocation));
  }

  @Test
  void falseWhenPlacementCategoryMediumRectangleAndNativeScreenLocation() {
    Stream.of(nativeTypeScreenLocations)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(
                        createMinimalPlacementDTO(
                            PlacementCategory.MEDIUM_RECTANGLE, screenLocation),
                        ctx),
                    "Expected false when placementCategory: MEDIUM_RECTANGLE and screenLocation: "
                        + screenLocation));
  }

  @Test
  void falseWhenPlacementCategoryNativeAndStaticScreenLocation() {
    Stream.of(staticTypeScreenLocationsButFullscreenVisible)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(
                        createMinimalPlacementDTO(PlacementCategory.NATIVE, screenLocation), ctx),
                    "Expected false when placementCategory:NATIVE and screenLocation: "
                        + screenLocation));
  }

  @Test
  void falseWhenPlacementCategoryNativeAndVideoScreenLocation() {
    Stream.of(videoTypeScreenLocations)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(
                        createMinimalPlacementDTO(PlacementCategory.NATIVE, screenLocation), ctx),
                    "Expected false when placementCategory:NATIVE and screenLocation: "
                        + screenLocation));
  }

  @Test
  void falseWhenPlacementCategoryInstreamVideoAndStaticScreenLocation() {
    Stream.of(staticTypeScreenLocationsButFullscreenVisible)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(
                        createMinimalPlacementDTO(PlacementCategory.INSTREAM_VIDEO, screenLocation),
                        ctx),
                    "Expected false when placementCategory:INSTREAM_VIDEO and screenLocation: "
                        + screenLocation));
  }

  @Test
  void falseWhenPlacementCategoryInterstitialAndStaticScreenLocation() {
    Stream.of(staticTypeScreenLocationsButFullscreenVisible)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(
                        createMinimalPlacementDTO(PlacementCategory.INTERSTITIAL, screenLocation),
                        ctx),
                    "Expected false when placementCategory:INTERSTITIAL and screenLocation: "
                        + screenLocation));
  }

  @Test
  void falseWhenPlacementCategoryRewardedVideoAndStaticScreenLocation() {
    Stream.of(staticTypeScreenLocationsButFullscreenVisible)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(
                        createMinimalPlacementDTO(PlacementCategory.REWARDED_VIDEO, screenLocation),
                        ctx),
                    "Expected false when placementCategory:REWARDED_VIDEO and screenLocation: "
                        + screenLocation));
  }

  @Test
  void trueWhenPlacementCategoryInArticleOrInFeedAndScreenLocationIsUnknown() {
    unknownPlacementCategories.stream()
        .forEach(
            placementCategory ->
                assertTrue(
                    validator.isValid(createMinimalPlacementDTO(placementCategory, UNKNOWN), ctx),
                    "Expected true when screenLocation: UNKNOWN and placementCategory: "
                        + placementCategory));
  }

  @Test
  void falseWhenPlacementCategoryInArticleAndScreenLocationIsNotUnknown() {
    Arrays.stream(ScreenLocation.values())
        .filter(screenLocation -> UNKNOWN != screenLocation)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(createMinimalPlacementDTO(IN_ARTICLE, screenLocation), ctx),
                    "Expected false when placementCategory: IN_ARTICLE and screenLocation: "
                        + screenLocation));
  }

  @Test
  void falseWhenPlacementCategoryInFeedAndScreenLocationIsNotUnknown() {
    Arrays.stream(ScreenLocation.values())
        .filter(screenLocation -> UNKNOWN != screenLocation)
        .forEach(
            screenLocation ->
                assertFalse(
                    validator.isValid(createMinimalPlacementDTO(IN_FEED, screenLocation), ctx),
                    "Expected false when placementCategory: IN_FEED and screenLocation: "
                        + screenLocation));
  }

  private PlacementDTO createMinimalPlacementDTO(
      PlacementCategory placementCategory, ScreenLocation screenLocation) {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setScreenLocation(screenLocation);
    placementDTO.setPlacementCategory(placementCategory);
    return placementDTO;
  }

  private void verifyValidationMessage(String expectedMessage) {
    verify(ctx).buildConstraintViolationWithTemplate(expectedMessage);
  }

  private void initializeContext() {
    ConstraintViolationBuilder constraintViolationBuilder = mock(ConstraintViolationBuilder.class);
    NodeBuilderCustomizableContext mockedNodeBuilderCustomizableContext =
        mock(NodeBuilderCustomizableContext.class);
    lenient()
        .when(constraintViolationBuilder.addPropertyNode(anyString()))
        .thenReturn(mockedNodeBuilderCustomizableContext);

    lenient()
        .when(ctx.buildConstraintViolationWithTemplate(any()))
        .thenReturn(constraintViolationBuilder);
  }

  private void initializeConstraint() {
    lenient()
        .when(placementDTOScreenLocationConstraint.message())
        .thenReturn("Interstitial boolean invalid");
    lenient()
        .when(placementDTOScreenLocationConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
    lenient().when(placementDTOScreenLocationConstraint.field()).thenReturn("screen location");
    ReflectionTestUtils.setField(validator, "annotation", placementDTOScreenLocationConstraint);
  }
}
