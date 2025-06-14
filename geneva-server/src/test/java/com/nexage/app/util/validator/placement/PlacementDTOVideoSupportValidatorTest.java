package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.PlacementCategory.MEDIUM_RECTANGLE;
import static com.nexage.admin.core.enums.VideoSupport.BANNER;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;
import static com.nexage.admin.core.enums.site.Type.APPLICATION;
import static com.nexage.admin.core.enums.site.Type.DOOH;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementDTOVideoSupportValidatorTest extends BaseValidatorTest {

  private final VideoSupport[] allVideoSupportButNative =
      new VideoSupport[] {BANNER, VIDEO_AND_BANNER, VIDEO};
  @Mock private PlacementDTOVideoSupportConstraint placementDTOVideoSupportConstraint;
  @InjectMocks private PlacementDTOVideoSupportValidator validator;

  @Test
  void falseWhenPlacementCategoryNullForAllSiteTypesAndAllVideoSupportTypes() {
    Stream.of(Type.values())
        .forEach(
            siteType ->
                Stream.of(VideoSupport.values())
                    .forEach(
                        videoSupport -> {
                          testIsValidReturnsFalseAndExpectedMessage(
                              siteType,
                              null,
                              videoSupport,
                              Platform.OTHER,
                              placementDTOVideoSupportConstraint.emptyMessage());
                        }));
  }

  @Test
  void trueWhenVideoSupportNullForAllSiteTypesAndAllPlacementCategories() {
    Stream.of(Type.values())
        .forEach(
            siteType ->
                Stream.of(PlacementCategory.values())
                    .forEach(
                        placementCategory -> {
                          PlacementDTO pl =
                              createMinimalPlacementDTO(
                                  siteType, placementCategory, null, Platform.OTHER);
                          assertTrue(validator.isValid(pl, ctx));
                        }));
  }

  @Test
  void falseWhenSiteTypeNullForAllVideoSupportTypesAndAllPlacementCategories() {
    Stream.of(VideoSupport.values())
        .forEach(
            videoSupport ->
                Stream.of(PlacementCategory.values())
                    .forEach(
                        placementCategory -> {
                          testIsValidReturnsFalseAndExpectedMessage(
                              null,
                              placementCategory,
                              videoSupport,
                              Platform.OTHER,
                              placementDTOVideoSupportConstraint.emptyMessage());
                        }));
  }

  @Test
  void falseWhenPlatformTypeNullForAllSiteAndAllVideoSupportAndAllPlacementCategories() {
    Stream.of(Type.values())
        .forEach(
            siteType ->
                Stream.of(PlacementCategory.values())
                    .forEach(
                        placementCategory ->
                            Stream.of(VideoSupport.values())
                                .forEach(
                                    videoSupport -> {
                                      testIsValidReturnsFalseAndExpectedMessage(
                                          siteType,
                                          placementCategory,
                                          videoSupport,
                                          null,
                                          placementDTOVideoSupportConstraint.emptyMessage());
                                    })));
  }

  @Test
  void trueWhenBannerAndVideoSupportBannerForAllSiteTypes() {
    Stream.of(Type.values())
        .forEach(
            siteType ->
                assertTrue(
                    validator.isValid(
                        createMinimalPlacementDTO(
                            siteType, PlacementCategory.BANNER, BANNER, Platform.OTHER),
                        ctx)));
  }

  @Test
  void trueWhenBannerAndVideoSupportNotBannerAndNotDoohSiteTypes() {
    Stream.of(Type.values())
        .filter(type -> DOOH != type)
        .forEach(
            siteType ->
                Stream.of(VIDEO, VIDEO_AND_BANNER)
                    .forEach(
                        videoSupport ->
                            assertTrue(
                                validator.isValid(
                                    createMinimalPlacementDTO(
                                        siteType,
                                        PlacementCategory.BANNER,
                                        videoSupport,
                                        Platform.OTHER),
                                    ctx))));
  }

  @Test
  void trueWhenInterstitialAndVideoSupportExceptNativeForAllSiteTypes() {
    Stream.of(Type.values())
        .forEach(
            siteType ->
                Stream.of(allVideoSupportButNative)
                    .forEach(
                        videoSupport ->
                            assertTrue(
                                validator.isValid(
                                    createMinimalPlacementDTO(
                                        siteType,
                                        PlacementCategory.INTERSTITIAL,
                                        videoSupport,
                                        Platform.OTHER),
                                    ctx))));
  }

  @Test
  void falseWhenBannerAndVideoSupportNotBannerForDOOH() {
    Stream.of(allVideoSupportButNative)
        .filter(videoSupport -> BANNER != videoSupport)
        .forEach(
            videoSupport ->
                testIsValidReturnsFalseAndExpectedMessage(
                    DOOH,
                    PlacementCategory.BANNER,
                    videoSupport,
                    Platform.OTHER,
                    placementDTOVideoSupportConstraint.message()));
  }

  @Test
  void trueWhenMediumRectangleAndVideoSupportExceptNativeForAllSiteTypesExceptDOOH() {
    Stream.of(Type.values())
        .filter(type -> DOOH != type)
        .forEach(
            siteType ->
                Stream.of(allVideoSupportButNative)
                    .forEach(
                        videoSupport ->
                            assertTrue(
                                validator.isValid(
                                    createMinimalPlacementDTO(
                                        siteType, MEDIUM_RECTANGLE, videoSupport, Platform.OTHER),
                                    ctx))));
  }

  @Test
  void falseWhenMediumRectangleAndDOOHForAnyVideoSupport() {
    Stream.of(VideoSupport.values())
        .forEach(
            videoSupport ->
                testIsValidReturnsFalseAndExpectedMessage(
                    DOOH,
                    MEDIUM_RECTANGLE,
                    videoSupport,
                    Platform.OTHER,
                    placementDTOVideoSupportConstraint.message()));
  }

  @Test
  void trueWhenInStreamVideoAndAllSiteType() {
    Stream.of(Type.values())
        .forEach(
            siteType ->
                assertTrue(
                    validator.isValid(
                        createMinimalPlacementDTO(siteType, INSTREAM_VIDEO, VIDEO, Platform.OTHER),
                        ctx)));
  }

  @Test
  void falseWhenInStreamNotVideoAndAllSiteType() {
    Stream.of(Type.values())
        .forEach(
            siteType ->
                Stream.of(VideoSupport.values())
                    .filter(videoSupport -> videoSupport != VIDEO)
                    .forEach(
                        videoSupport ->
                            testIsValidReturnsFalseAndExpectedMessage(
                                siteType,
                                INSTREAM_VIDEO,
                                videoSupport,
                                Platform.OTHER,
                                placementDTOVideoSupportConstraint.message())));
  }

  @Test
  void falseWhenNativePlacementAndAllVideoSupportAndSiteTypeNotApplication() {
    Stream.of(Type.values())
        .filter(siteType -> APPLICATION != siteType)
        .forEach(
            siteType ->
                Stream.of(VideoSupport.values())
                    .forEach(
                        videoSupport ->
                            testIsValidReturnsFalseAndExpectedMessage(
                                siteType,
                                PlacementCategory.NATIVE_V2,
                                videoSupport,
                                Platform.OTHER,
                                placementDTOVideoSupportConstraint.message())));
  }

  @Test
  void falseWhenNativeV2PlacementAndAllVideoSupportAndSiteTypeNotApplication() {
    Stream.of(Type.values())
        .filter(siteType -> APPLICATION != siteType)
        .forEach(
            siteType ->
                Stream.of(VideoSupport.values())
                    .forEach(
                        videoSupport ->
                            testIsValidReturnsFalseAndExpectedMessage(
                                siteType,
                                PlacementCategory.NATIVE_V2,
                                videoSupport,
                                Platform.OTHER,
                                placementDTOVideoSupportConstraint.message())));
  }

  @Test
  void falseWhenInArticleNotVideoAndAllSiteType() {
    Stream.of(Type.values())
        .forEach(
            siteType ->
                Stream.of(VideoSupport.values())
                    .filter(videoSupport -> videoSupport != VIDEO)
                    .forEach(
                        videoSupport ->
                            testIsValidReturnsFalseAndExpectedMessage(
                                siteType,
                                IN_ARTICLE,
                                videoSupport,
                                Platform.OTHER,
                                placementDTOVideoSupportConstraint.message())));
  }

  @Test
  void trueWhenInArticleAndVideoAndAllSiteTypeExceptDooh() {
    Stream.of(Type.values())
        .filter(type -> DOOH != type)
        .forEach(
            siteType ->
                assertTrue(
                    validator.isValid(
                        createMinimalPlacementDTO(siteType, IN_ARTICLE, VIDEO, Platform.OTHER),
                        ctx)));
  }

  @Test
  void falseWhenInFeedNotVideoAndAllSiteType() {
    Stream.of(Type.values())
        .forEach(
            siteType ->
                Stream.of(VideoSupport.values())
                    .filter(videoSupport -> videoSupport != VIDEO)
                    .forEach(
                        videoSupport ->
                            testIsValidReturnsFalseAndExpectedMessage(
                                siteType,
                                IN_FEED,
                                videoSupport,
                                Platform.OTHER,
                                placementDTOVideoSupportConstraint.message())));
  }

  @Test
  void trueWhenInFeedAndVideoAndAllSiteTypeExceptDooh() {
    Stream.of(Type.values())
        .filter(type -> DOOH != type)
        .forEach(
            siteType ->
                assertTrue(
                    validator.isValid(
                        createMinimalPlacementDTO(siteType, IN_FEED, VIDEO, Platform.OTHER), ctx)));
  }

  @Test
  void falseWhenApplicationAndCtvOttAndVideoAndNotInstream() {
    Stream.of(PlacementCategory.values())
        .filter(placementCategory -> placementCategory != INSTREAM_VIDEO)
        .forEach(
            placementCategory ->
                testIsValidReturnsFalseAndExpectedMessage(
                    APPLICATION,
                    placementCategory,
                    VIDEO,
                    Platform.CTV_OTT,
                    placementDTOVideoSupportConstraint.message()));
  }

  @Test
  void falseWhenApplicationAndCtvOttAndInstreamButNotVideo() {
    Stream.of(VideoSupport.values())
        .filter(videoSupport -> videoSupport != VIDEO)
        .forEach(
            videoSupport ->
                testIsValidReturnsFalseAndExpectedMessage(
                    APPLICATION,
                    INSTREAM_VIDEO,
                    videoSupport,
                    Platform.CTV_OTT,
                    placementDTOVideoSupportConstraint.message()));
  }

  @Test
  void trueWhenApplicationAndCtvOttAndInstreamAndVideo() {
    assertTrue(
        validator.isValid(
            createMinimalPlacementDTO(APPLICATION, INSTREAM_VIDEO, VIDEO, Platform.CTV_OTT), ctx));
  }

  private PlacementDTO createMinimalPlacementDTO(
      Type siteType,
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      Platform platformType) {
    SiteDTO site = new SiteDTO();
    site.setType(siteType);
    site.setPlatform(platformType);

    PlacementDTO position = new PlacementDTO();
    position.setSite(site);
    position.setPlacementCategory(placementCategory);
    position.setVideoSupport(videoSupport);
    return position;
  }

  private void verifyValidationMessage(String expectedMessage) {
    verify(ctx).buildConstraintViolationWithTemplate(expectedMessage);
  }

  private void testIsValidReturnsFalseAndExpectedMessage(
      Type siteType,
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      Platform platformType,
      String message) {
    PlacementDTO placementDTO =
        createMinimalPlacementDTO(siteType, placementCategory, videoSupport, platformType);
    assertFalse(
        validator.isValid(placementDTO, ctx),
        String.format(
            "Expected false when siteType: %s and placementCategory: %s and videoSupport: %s and platform type: %s",
            siteType, placementCategory, videoSupport, platformType));
    verifyValidationMessage(message);
    reset(ctx);
    initializeContext();
  }

  @Override
  public void initializeConstraint() {
    lenient()
        .when(placementDTOVideoSupportConstraint.message())
        .thenReturn("Invalid for specified site type, platform type and/or placementCategory");
    lenient()
        .when(placementDTOVideoSupportConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
    lenient().when(placementDTOVideoSupportConstraint.field()).thenReturn("videoSupport");
  }
}
