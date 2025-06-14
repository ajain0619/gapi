package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.BANNER;
import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.PlacementCategory.INTERSTITIAL;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.PlacementCategory.MEDIUM_RECTANGLE;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE_V2;
import static com.nexage.admin.core.enums.site.Type.APPLICATION;
import static com.nexage.admin.core.enums.site.Type.DESKTOP;
import static com.nexage.admin.core.enums.site.Type.DOOH;
import static com.nexage.admin.core.enums.site.Type.MOBILE_WEB;
import static com.nexage.admin.core.enums.site.Type.WEBSITE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementDTOCategoryValidatorTest extends BaseValidatorTest {

  @Mock private PlacementDTOCategoryConstraint placementDTOCategoryConstraint;
  @InjectMocks private PlacementDTOCategoryValidator validator;
  private final String message = "Invalid placementCategory for site type and/or platform type";

  private Set<PlacementCategory> applicationPlacementCategories =
      ImmutableSet.of(
          BANNER, INTERSTITIAL, MEDIUM_RECTANGLE, NATIVE_V2, INSTREAM_VIDEO, IN_ARTICLE, IN_FEED);
  private Set<PlacementCategory> desktopOrMobileWebOrWebsitePlacementCategories =
      ImmutableSet.of(BANNER, INTERSTITIAL, MEDIUM_RECTANGLE, INSTREAM_VIDEO, IN_ARTICLE, IN_FEED);
  private Set<PlacementCategory> doohPlacementCategories =
      ImmutableSet.of(BANNER, INTERSTITIAL, INSTREAM_VIDEO);

  @Test
  void falseWhenPlacementCategoryNull() {
    assertFalse(validator.isValid(createPlacementDTO(MOBILE_WEB, null, Platform.OTHER), ctx));
    verifyValidationMessage(ValidationMessages.WRONG_IS_EMPTY);
  }

  @Test
  void falseWhenSiteTypeIsNull() {
    assertFalse(validator.isValid(createPlacementDTO(null, BANNER, Platform.OTHER), ctx));
    verifyValidationMessage(ValidationMessages.WRONG_IS_EMPTY);
  }

  @Test
  void falseWhenPlatformTypeIsNull() {
    assertFalse(validator.isValid(createPlacementDTO(MOBILE_WEB, BANNER, null), ctx));
    verifyValidationMessage(ValidationMessages.WRONG_IS_EMPTY);
  }

  @Test
  void trueWhenPlacementCategoryValidForSiteTypeApplication() {
    applicationPlacementCategories.stream()
        .forEach(
            placementCategory ->
                assertValidatorTrue(
                    createPlacementDTO(APPLICATION, placementCategory, Platform.OTHER)));
  }

  @Test
  void falseWhenPlacementCategoryNotValidForSiteTypeApplication() {
    Arrays.stream(PlacementCategory.values())
        .filter(placementCategory -> !applicationPlacementCategories.contains(placementCategory))
        .forEach(
            placementCategory ->
                assertValidatorFalse(
                    createPlacementDTO(APPLICATION, placementCategory, Platform.OTHER), message));
  }

  @Test
  void trueWhenPlacementCategoryValidForSiteTypeDesktop() {
    desktopOrMobileWebOrWebsitePlacementCategories.stream()
        .forEach(
            placementCategory ->
                assertValidatorTrue(
                    createPlacementDTO(DESKTOP, placementCategory, Platform.OTHER)));
  }

  @Test
  void falseWhenPlacementCategoryNotValidForSiteTypeDesktop() {
    Arrays.stream(PlacementCategory.values())
        .filter(
            placementCategory ->
                !desktopOrMobileWebOrWebsitePlacementCategories.contains(placementCategory))
        .forEach(
            placementCategory ->
                assertValidatorFalse(
                    createPlacementDTO(DESKTOP, placementCategory, Platform.OTHER), message));
  }

  @Test
  void trueWhenPlacementCategoryValidForSiteTypeMobileWebsite() {
    desktopOrMobileWebOrWebsitePlacementCategories.stream()
        .forEach(
            placementCategory ->
                assertValidatorTrue(
                    createPlacementDTO(MOBILE_WEB, placementCategory, Platform.OTHER)));
  }

  @Test
  void falseWhenPlacementCategoryNotValidForSiteTypeMobileWebsite() {
    Arrays.stream(PlacementCategory.values())
        .filter(
            placementCategory ->
                !desktopOrMobileWebOrWebsitePlacementCategories.contains(placementCategory))
        .forEach(
            placementCategory ->
                assertValidatorFalse(
                    createPlacementDTO(MOBILE_WEB, placementCategory, Platform.OTHER), message));
  }

  @Test
  void trueWhenPlacementCategoryValidForSiteTypeDOOH() {
    doohPlacementCategories.stream()
        .forEach(
            placementCategory ->
                assertValidatorTrue(createPlacementDTO(DOOH, placementCategory, Platform.OTHER)));
  }

  @Test
  void falseWhenPlacementCategoryNotValidForSiteTypeDOOH() {
    Arrays.stream(PlacementCategory.values())
        .filter(placementCategory -> !doohPlacementCategories.contains(placementCategory))
        .forEach(
            placementCategory ->
                assertValidatorFalse(
                    createPlacementDTO(DOOH, placementCategory, Platform.OTHER), message));
  }

  @Test
  void trueWhenPlacementCategoryValidForSiteTypeWebsite() {
    desktopOrMobileWebOrWebsitePlacementCategories.stream()
        .forEach(
            placementCategory ->
                assertValidatorTrue(
                    createPlacementDTO(WEBSITE, placementCategory, Platform.OTHER)));
  }

  @Test
  void falseWhenPlacementCategoryNotValidForSiteTypeWebsite() {
    Arrays.stream(PlacementCategory.values())
        .filter(
            placementCategory ->
                !desktopOrMobileWebOrWebsitePlacementCategories.contains(placementCategory))
        .forEach(
            placementCategory ->
                assertValidatorFalse(
                    createPlacementDTO(WEBSITE, placementCategory, Platform.OTHER), message));
  }

  @Test
  void falseWhenPlacementCategoryNotInstreamForPlatformCtvOtt() {
    Arrays.stream(PlacementCategory.values())
        .filter(placementCategory -> placementCategory != INSTREAM_VIDEO)
        .forEach(
            placementCategory ->
                assertValidatorFalse(
                    createPlacementDTO(APPLICATION, placementCategory, Platform.CTV_OTT), message));
  }

  @Test
  void trueWhenPlacementCategoryInstreamForPlatformCtvOtt() {
    assertValidatorTrue(createPlacementDTO(APPLICATION, INSTREAM_VIDEO, Platform.CTV_OTT));
  }

  private void assertValidatorFalse(PlacementDTO placementDTO, String expectedMessage) {
    assertFalse(
        validator.isValid(placementDTO, ctx),
        String.format(
            "Expected false for placementCategory: [%s] , siteType: [%s] and platformType: [%s]",
            placementDTO.getPlacementCategory(),
            placementDTO.getSite().getType(),
            placementDTO.getSite().getPlatform()));
    verifyValidationMessage(expectedMessage);
    reset(ctx);
    initializeContext();
  }

  private void assertValidatorTrue(PlacementDTO placementDTO) {
    assertTrue(
        validator.isValid(placementDTO, ctx),
        String.format(
            "Expected true for placementCategory: [%s] , siteType: [%s] and platformType: [%s]",
            placementDTO.getPlacementCategory(),
            placementDTO.getSite().getType(),
            placementDTO.getSite().getPlatform()));
    verify(ctx, never()).buildConstraintViolationWithTemplate(anyString());
  }

  private PlacementDTO createPlacementDTO(
      Type siteType, PlacementCategory placementCategory, Platform platformType) {
    SiteDTO site = new SiteDTO();
    site.setType(siteType);
    site.setPlatform(platformType);
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setPlacementCategory(placementCategory);
    placementDTO.setSite(site);
    return placementDTO;
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
