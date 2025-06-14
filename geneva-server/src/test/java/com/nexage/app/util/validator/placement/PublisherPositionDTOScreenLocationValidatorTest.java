package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.ScreenLocation.UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

class PublisherPositionDTOScreenLocationValidatorTest extends BaseValidatorTest {

  private Set<PlacementCategory> unknownPlacementCategories = ImmutableSet.of(IN_ARTICLE, IN_FEED);

  @Mock private PlacementDTOScreenLocationConstraint placementDTOScreenLocationConstraint;
  @InjectMocks private PublisherPositionDTOScreenLocationValidator validator;

  @Test
  void falseWhenPlacementCategoryNull() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setPlacementCategory(null);
    publisherPositionDTO.setScreenLocation(UNKNOWN);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
    verifyValidationMessage(ValidationMessages.WRONG_IS_EMPTY);
  }

  @Test
  void trueWhenScreenLocationNull() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setPlacementCategory(PlacementCategory.BANNER);
    publisherPositionDTO.setScreenLocation(null);
    assertTrue(validator.isValid(publisherPositionDTO, ctx));
  }

  @Test
  void trueWhenPlacementCategoryInArticleOrInFeedAndScreenLocationIsUnknown() {
    unknownPlacementCategories.stream()
        .forEach(
            placementCategory ->
                assertTrue(
                    validator.isValid(
                        createMinimalPublisherPositionDTO(placementCategory, UNKNOWN), ctx),
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
                    validator.isValid(
                        createMinimalPublisherPositionDTO(IN_ARTICLE, screenLocation), ctx),
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
                    validator.isValid(
                        createMinimalPublisherPositionDTO(IN_FEED, screenLocation), ctx),
                    "Expected false when placementCategory: IN_FEED and screenLocation: "
                        + screenLocation));
  }

  private PublisherPositionDTO createMinimalPublisherPositionDTO(
      PlacementCategory placementCategory, ScreenLocation screenLocation) {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setScreenLocation(screenLocation);
    publisherPositionDTO.setPlacementCategory(placementCategory);
    return publisherPositionDTO;
  }

  private void verifyValidationMessage(String expectedMessage) {
    verify(ctx).buildConstraintViolationWithTemplate(expectedMessage);
  }

  @Override
  public void initializeConstraint() {
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
