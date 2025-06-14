package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.BANNER;
import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.PlacementCategory.INTERSTITIAL;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.PlacementCategory.MEDIUM_RECTANGLE;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE_V2;
import static com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType.APPLICATION;
import static com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType.DESKTOP;
import static com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType.DOOH;
import static com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType.MOBILE_WEB;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

class PublisherPositionDTOCategoryValidatorTest extends BaseValidatorTest {

  @Mock private PlacementDTOCategoryConstraint placementDTOCategoryConstraint;
  @InjectMocks private PublisherPositionDTOCategoryValidator validator;
  @InjectMocks private PlacementDTOCategoryValidator placementDTOCategoryValidator;
  private final String message = "Invalid placementCategory for site and/or platform type";

  private Set<PlacementCategory> applicationPlacementCategories =
      ImmutableSet.of(
          BANNER, INTERSTITIAL, MEDIUM_RECTANGLE, NATIVE_V2, INSTREAM_VIDEO, IN_ARTICLE, IN_FEED);
  private Set<PlacementCategory> desktopOrMobileWebPlacementCategories =
      ImmutableSet.of(BANNER, INTERSTITIAL, MEDIUM_RECTANGLE, INSTREAM_VIDEO, IN_ARTICLE, IN_FEED);
  private Set<PlacementCategory> doohPlacementCategories =
      ImmutableSet.of(BANNER, INTERSTITIAL, INSTREAM_VIDEO);

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(
        validator, "placementDTOCategoryValidator", placementDTOCategoryValidator);
  }

  @Test
  public void falseWhenPlacementCategoryNull() {
    assertFalse(
        validator.isValid(
            createPublisherPositionDTO(MOBILE_WEB, null, PublisherSiteDTO.Platform.OTHER), ctx));
    verifyValidationMessage(ValidationMessages.WRONG_IS_EMPTY);
  }

  @Test
  public void falseWhenSiteTypeIsNull() {
    assertFalse(
        validator.isValid(
            createPublisherPositionDTO(null, BANNER, PublisherSiteDTO.Platform.OTHER), ctx));
    verifyValidationMessage(ValidationMessages.WRONG_IS_EMPTY);
  }

  @Test
  public void falseWhenPlatformTypeIsNull() {
    assertFalse(validator.isValid(createPublisherPositionDTO(MOBILE_WEB, BANNER, null), ctx));
    verifyValidationMessage(ValidationMessages.WRONG_IS_EMPTY);
  }

  @Test
  public void trueWhenPlacementCategoryValidForSiteTypeApplication() {
    applicationPlacementCategories.stream()
        .forEach(
            placementCategory ->
                assertValidatorTrue(
                    createPublisherPositionDTO(
                        APPLICATION, placementCategory, PublisherSiteDTO.Platform.ANDROID)));
  }

  @Test
  public void falseWhenPlacementCategoryNotValidForSiteTypeApplication() {
    Arrays.stream(PlacementCategory.values())
        .filter(placementCategory -> !applicationPlacementCategories.contains(placementCategory))
        .forEach(
            placementCategory ->
                assertValidatorFalse(
                    createPublisherPositionDTO(
                        APPLICATION, placementCategory, PublisherSiteDTO.Platform.ANDROID),
                    message));
  }

  @Test
  public void trueWhenPlacementCategoryValidForSiteTypeDesktop() {
    desktopOrMobileWebPlacementCategories.stream()
        .forEach(
            placementCategory ->
                assertValidatorTrue(
                    createPublisherPositionDTO(
                        DESKTOP, placementCategory, PublisherSiteDTO.Platform.OTHER)));
  }

  @Test
  public void falseWhenPlacementCategoryNotValidForSiteTypeDesktop() {
    Arrays.stream(PlacementCategory.values())
        .filter(
            placementCategory -> !desktopOrMobileWebPlacementCategories.contains(placementCategory))
        .forEach(
            placementCategory ->
                assertValidatorFalse(
                    createPublisherPositionDTO(
                        DESKTOP, placementCategory, PublisherSiteDTO.Platform.OTHER),
                    message));
  }

  @Test
  public void trueWhenPlacementCategoryValidForSiteTypeMobileWebsite() {
    desktopOrMobileWebPlacementCategories.stream()
        .forEach(
            placementCategory ->
                assertValidatorTrue(
                    createPublisherPositionDTO(
                        MOBILE_WEB, placementCategory, PublisherSiteDTO.Platform.OTHER)));
  }

  @Test
  public void falseWhenPlacementCategoryNotValidForSiteTypeMobileWebsite() {
    Arrays.stream(PlacementCategory.values())
        .filter(
            placementCategory -> !desktopOrMobileWebPlacementCategories.contains(placementCategory))
        .forEach(
            placementCategory ->
                assertValidatorFalse(
                    createPublisherPositionDTO(
                        MOBILE_WEB, placementCategory, PublisherSiteDTO.Platform.OTHER),
                    message));
  }

  @Test
  public void trueWhenPlacementCategoryValidForSiteTypeDOOH() {
    doohPlacementCategories.stream()
        .forEach(
            placementCategory ->
                assertValidatorTrue(
                    createPublisherPositionDTO(
                        DOOH, placementCategory, PublisherSiteDTO.Platform.OTHER)));
  }

  @Test
  public void falseWhenPlacementCategoryNotValidForSiteTypeDOOH() {
    Arrays.stream(PlacementCategory.values())
        .filter(placementCategory -> !doohPlacementCategories.contains(placementCategory))
        .forEach(
            placementCategory ->
                assertValidatorFalse(
                    createPublisherPositionDTO(
                        DOOH, placementCategory, PublisherSiteDTO.Platform.OTHER),
                    message));
  }

  @Test
  void falseWhenPlacementCategoryNotInstreamForPlatformCtvOtt() {
    Arrays.stream(PlacementCategory.values())
        .filter(placementCategory -> placementCategory != INSTREAM_VIDEO)
        .forEach(
            placementCategory ->
                assertValidatorFalse(
                    createPublisherPositionDTO(
                        APPLICATION, placementCategory, PublisherSiteDTO.Platform.CTV_OTT),
                    message));
  }

  @Test
  void trueWhenPlacementCategoryInstreamForPlatformCtvOtt() {
    assertValidatorTrue(
        createPublisherPositionDTO(APPLICATION, INSTREAM_VIDEO, PublisherSiteDTO.Platform.CTV_OTT));
  }

  private void assertValidatorFalse(
      PublisherPositionDTO publisherPositionDTO, String expectedMessage) {
    assertFalse(
        validator.isValid(publisherPositionDTO, ctx),
        String.format(
            "Expected false for placementCategory: [%s] , siteType [%s] and platformType [%s]",
            publisherPositionDTO.getPlacementCategory(),
            publisherPositionDTO.getSite().getType(),
            publisherPositionDTO.getSite().getPlatform()));
    verifyValidationMessage(expectedMessage);
    reset(ctx);
    initializeContext();
  }

  private void assertValidatorTrue(PublisherPositionDTO publisherPositionDTO) {
    assertTrue(
        validator.isValid(publisherPositionDTO, ctx),
        String.format(
            "Expected true for placementCategory: [%s] , siteType [%s] and platformType [%s]",
            publisherPositionDTO.getPlacementCategory(),
            publisherPositionDTO.getSite().getType(),
            publisherPositionDTO.getSite().getPlatform()));
    verify(ctx, never()).buildConstraintViolationWithTemplate(anyString());
  }

  private PublisherPositionDTO createPublisherPositionDTO(
      PublisherSiteDTO.SiteType siteType,
      PlacementCategory placementCategory,
      PublisherSiteDTO.Platform platformType) {
    PublisherSiteDTO site = new PublisherSiteDTO();
    site.setType(siteType);
    site.setPlatform(platformType);
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setPlacementCategory(placementCategory);
    publisherPositionDTO.setSite(site);
    return publisherPositionDTO;
  }

  private void verifyValidationMessage(String expectedMessage) {
    verify(ctx).buildConstraintViolationWithTemplate(expectedMessage);
  }

  @Override
  public void initializeConstraint() {
    lenient().when(placementDTOCategoryConstraint.message()).thenReturn(message);
    lenient()
        .when(placementDTOCategoryConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
    lenient().when(placementDTOCategoryConstraint.field()).thenReturn("placementCategory");
  }
}
