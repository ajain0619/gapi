package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.IN_FEED;
import static com.nexage.admin.core.enums.PlacementCategory.MEDIUM_RECTANGLE;
import static com.nexage.admin.core.enums.VideoSupport.BANNER;
import static com.nexage.admin.core.enums.VideoSupport.NATIVE;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;
import static com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType.APPLICATION;
import static com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType.DOOH;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

class PublisherPositionDTOVideoSupportValidatorTest extends BaseValidatorTest {

  private final VideoSupport[] allVideoSupportButNative =
      new VideoSupport[] {BANNER, VIDEO_AND_BANNER, VIDEO};
  @Mock private PlacementDTOVideoSupportConstraint placementDTOVideoSupportConstraint;
  @InjectMocks private PublisherPositionDTOVideoSupportValidator validator;
  @InjectMocks private PlacementDTOVideoSupportValidator placementDTOVideoSupportValidator;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(
        validator, "placementDTOVideoSupportValidator", placementDTOVideoSupportValidator);
  }

  @Test
  void shouldReturnFalseWhenPlacementCategoryNullForAllSiteTypesAndAllVideoSupportTypes() {
    Stream.of(PublisherSiteDTO.SiteType.values())
        .forEach(
            siteType ->
                Stream.of(VideoSupport.values())
                    .forEach(
                        videoSupport -> {
                          testIsValidReturnsFalseAndExpectedMessage(
                              siteType,
                              null,
                              videoSupport,
                              PublisherSiteDTO.Platform.OTHER,
                              placementDTOVideoSupportConstraint.emptyMessage());
                        }));
  }

  @Test
  void shouldReturnTrueWhenVideoSupportNullForAllSiteTypesAndAllPlacementCategories() {
    Stream.of(PublisherSiteDTO.SiteType.values())
        .forEach(
            siteType ->
                Stream.of(PlacementCategory.values())
                    .forEach(
                        placementCategory -> {
                          PublisherPositionDTO publisherPositionDTO =
                              createMinimalPublisherPositionDTO(
                                  siteType,
                                  placementCategory,
                                  null,
                                  PublisherSiteDTO.Platform.OTHER);
                          assertTrue(validator.isValid(publisherPositionDTO, ctx));
                        }));
  }

  @Test
  void shouldReturnFalseWhenSiteTypeNullForAllVideoSupportTypesAndAllPlacementCategories() {
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
                              PublisherSiteDTO.Platform.OTHER,
                              placementDTOVideoSupportConstraint.emptyMessage());
                        }));
  }

  @Test
  void
      shouldReturnFalseWhenPlatformTypeNullForAllSiteAndAllVideoSupportAndAllPlacementCategories() {
    Stream.of(PublisherSiteDTO.SiteType.values())
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
  void shouldReturnTrueWhenBannerAndVideoSupportBannerForAllSiteTypes() {
    Stream.of(PublisherSiteDTO.SiteType.values())
        .forEach(
            siteType ->
                assertTrue(
                    validator.isValid(
                        createMinimalPublisherPositionDTO(
                            siteType,
                            PlacementCategory.BANNER,
                            BANNER,
                            PublisherSiteDTO.Platform.OTHER),
                        ctx)));
  }

  @Test
  void shouldReturnTrueWhenBannerAndVideoSupportNotBannerAndNotDoohSiteTypes() {
    Stream.of(PublisherSiteDTO.SiteType.values())
        .filter(type -> DOOH != type)
        .forEach(
            siteType ->
                Stream.of(VIDEO, VIDEO_AND_BANNER)
                    .forEach(
                        videoSupport ->
                            assertTrue(
                                validator.isValid(
                                    createMinimalPublisherPositionDTO(
                                        siteType,
                                        PlacementCategory.BANNER,
                                        videoSupport,
                                        PublisherSiteDTO.Platform.OTHER),
                                    ctx))));
  }

  @Test
  void shouldReturnTrueWhenInterstitialAndVideoSupportExceptNativeForAllSiteTypes() {
    Stream.of(PublisherSiteDTO.SiteType.values())
        .forEach(
            siteType ->
                Stream.of(allVideoSupportButNative)
                    .forEach(
                        videoSupport ->
                            assertTrue(
                                validator.isValid(
                                    createMinimalPublisherPositionDTO(
                                        siteType,
                                        PlacementCategory.INTERSTITIAL,
                                        videoSupport,
                                        PublisherSiteDTO.Platform.OTHER),
                                    ctx))));
  }

  @Test
  void shouldReturnFalseWhenBannerAndVideoSupportNotBannerForDOOH() {
    Stream.of(allVideoSupportButNative)
        .filter(videoSupport -> BANNER != videoSupport)
        .forEach(
            videoSupport ->
                testIsValidReturnsFalseAndExpectedMessage(
                    DOOH,
                    PlacementCategory.BANNER,
                    videoSupport,
                    PublisherSiteDTO.Platform.OTHER,
                    placementDTOVideoSupportConstraint.message()));
  }

  @Test
  void shouldReturnTrueWhenMediumRectangleAndVideoSupportExceptNativeForAllSiteTypesExceptDOOH() {
    Stream.of(PublisherSiteDTO.SiteType.values())
        .filter(type -> DOOH != type)
        .forEach(
            siteType ->
                Stream.of(allVideoSupportButNative)
                    .forEach(
                        videoSupport ->
                            assertTrue(
                                validator.isValid(
                                    createMinimalPublisherPositionDTO(
                                        siteType,
                                        MEDIUM_RECTANGLE,
                                        videoSupport,
                                        PublisherSiteDTO.Platform.OTHER),
                                    ctx))));
  }

  @Test
  void shouldReturnFalseWhenMediumRectangleAndVideoSupportNativeForAllSiteTypes() {
    Stream.of(PublisherSiteDTO.SiteType.values())
        .forEach(
            siteType ->
                assertFalse(
                    validator.isValid(
                        createMinimalPublisherPositionDTO(
                            siteType, MEDIUM_RECTANGLE, NATIVE, PublisherSiteDTO.Platform.OTHER),
                        ctx)));
  }

  @Test
  void shouldReturnFalseWhenMediumRectangleAndDOOHForAnyVideoSupport() {
    Stream.of(VideoSupport.values())
        .forEach(
            videoSupport ->
                testIsValidReturnsFalseAndExpectedMessage(
                    DOOH,
                    MEDIUM_RECTANGLE,
                    videoSupport,
                    PublisherSiteDTO.Platform.OTHER,
                    placementDTOVideoSupportConstraint.message()));
  }

  @Test
  void shouldReturnTrueWhenInStreamVideoAndAllSiteType() {
    Stream.of(PublisherSiteDTO.SiteType.values())
        .forEach(
            siteType ->
                assertTrue(
                    validator.isValid(
                        createMinimalPublisherPositionDTO(
                            siteType, INSTREAM_VIDEO, VIDEO, PublisherSiteDTO.Platform.OTHER),
                        ctx)));
  }

  @Test
  void shouldReturnFalseWhenInStreamNotVideoAndAllSiteType() {
    Stream.of(PublisherSiteDTO.SiteType.values())
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
                                PublisherSiteDTO.Platform.OTHER,
                                placementDTOVideoSupportConstraint.message())));
  }

  @Test
  void shouldReturnFalseWhenNativeAndVideoSupportNotNativeForApplicationSiteType() {
    Stream.of(allVideoSupportButNative)
        .forEach(
            videoSupport ->
                testIsValidReturnsFalseAndExpectedMessage(
                    APPLICATION,
                    PlacementCategory.NATIVE,
                    videoSupport,
                    PublisherSiteDTO.Platform.ANDROID,
                    placementDTOVideoSupportConstraint.message()));
  }

  @Test
  void shouldReturnFalseWhenNativePlacementAndAllVideoSupportAndSiteTypeNotApplication() {
    Stream.of(PublisherSiteDTO.SiteType.values())
        .filter(siteType -> APPLICATION != siteType)
        .forEach(
            siteType ->
                Stream.of(VideoSupport.values())
                    .forEach(
                        videoSupport ->
                            testIsValidReturnsFalseAndExpectedMessage(
                                siteType,
                                PlacementCategory.NATIVE,
                                videoSupport,
                                PublisherSiteDTO.Platform.OTHER,
                                placementDTOVideoSupportConstraint.message())));
  }

  @Test
  void shouldReturnFalseWhenNativeV2AndVideoSupportNotNativeForApplicationSiteType() {
    Stream.of(allVideoSupportButNative)
        .forEach(
            videoSupport ->
                testIsValidReturnsFalseAndExpectedMessage(
                    APPLICATION,
                    PlacementCategory.NATIVE_V2,
                    videoSupport,
                    PublisherSiteDTO.Platform.ANDROID,
                    placementDTOVideoSupportConstraint.message()));
  }

  @Test
  void shouldReturnTrueWhenNativeV2AndVideoSupportNativeForApplicationSiteType() {
    assertTrue(
        validator.isValid(
            createMinimalPublisherPositionDTO(
                APPLICATION,
                PlacementCategory.NATIVE_V2,
                NATIVE,
                PublisherSiteDTO.Platform.ANDROID),
            ctx));
    verify(ctx, never()).buildConstraintViolationWithTemplate(anyString());
  }

  @Test
  void shouldReturnFalseWhenNativeV2PlacementAndAllVideoSupportAndSiteTypeNotApplication() {
    Stream.of(PublisherSiteDTO.SiteType.values())
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
                                PublisherSiteDTO.Platform.OTHER,
                                placementDTOVideoSupportConstraint.message())));
  }

  @Test
  void shouldReturnFalseWhenInArticleNotVideoAndAllSiteType() {
    Stream.of(PublisherSiteDTO.SiteType.values())
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
                                PublisherSiteDTO.Platform.OTHER,
                                placementDTOVideoSupportConstraint.message())));
  }

  @Test
  void shouldReturnTrueWhenInArticleAndVideoAndAllSiteTypeExceptDooh() {
    Stream.of(PublisherSiteDTO.SiteType.values())
        .filter(type -> DOOH != type)
        .forEach(
            siteType ->
                assertTrue(
                    validator.isValid(
                        createMinimalPublisherPositionDTO(
                            siteType, IN_ARTICLE, VIDEO, PublisherSiteDTO.Platform.OTHER),
                        ctx)));
  }

  @Test
  void shouldReturnFalseWhenInFeedNotVideoAndAllSiteType() {
    Stream.of(PublisherSiteDTO.SiteType.values())
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
                                PublisherSiteDTO.Platform.OTHER,
                                placementDTOVideoSupportConstraint.message())));
  }

  @Test
  void shouldReturnTrueWhenInFeedAndVideoAndAllSiteTypeExceptDooh() {
    Stream.of(PublisherSiteDTO.SiteType.values())
        .filter(type -> DOOH != type)
        .forEach(
            siteType ->
                assertTrue(
                    validator.isValid(
                        createMinimalPublisherPositionDTO(
                            siteType, IN_FEED, VIDEO, PublisherSiteDTO.Platform.OTHER),
                        ctx)));
  }

  @Test
  void shouldReturnFalseWhenApplicationAndCtvOttAndVideoAndNotInstream() {
    Stream.of(PlacementCategory.values())
        .filter(placementCategory -> placementCategory != INSTREAM_VIDEO)
        .forEach(
            placementCategory ->
                testIsValidReturnsFalseAndExpectedMessage(
                    APPLICATION,
                    placementCategory,
                    VIDEO,
                    PublisherSiteDTO.Platform.CTV_OTT,
                    placementDTOVideoSupportConstraint.message()));
  }

  @Test
  void shouldReturnFalseWhenApplicationAndCtvOttAndInstreamButNotVideo() {
    Stream.of(VideoSupport.values())
        .filter(videoSupport -> videoSupport != VIDEO)
        .forEach(
            videoSupport ->
                testIsValidReturnsFalseAndExpectedMessage(
                    APPLICATION,
                    INSTREAM_VIDEO,
                    videoSupport,
                    PublisherSiteDTO.Platform.CTV_OTT,
                    placementDTOVideoSupportConstraint.message()));
  }

  @Test
  void shouldReturnTrueWhenApplicationAndCtvOttAndInstreamAndVideo() {
    assertTrue(
        validator.isValid(
            createMinimalPublisherPositionDTO(
                APPLICATION, INSTREAM_VIDEO, VIDEO, PublisherSiteDTO.Platform.CTV_OTT),
            ctx));
  }

  private PublisherPositionDTO createMinimalPublisherPositionDTO(
      PublisherSiteDTO.SiteType siteType,
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      PublisherSiteDTO.Platform platformType) {
    PublisherSiteDTO site = new PublisherSiteDTO();
    site.setType(siteType);
    site.setPlatform(platformType);

    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setSite(site);
    publisherPositionDTO.setPlacementCategory(placementCategory);
    publisherPositionDTO.setVideoSupport(videoSupport);
    return publisherPositionDTO;
  }

  private void verifyValidationMessage(String expectedMessage) {
    verify(ctx).buildConstraintViolationWithTemplate(expectedMessage);
  }

  private void testIsValidReturnsFalseAndExpectedMessage(
      PublisherSiteDTO.SiteType siteType,
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      PublisherSiteDTO.Platform platformType,
      String message) {
    PublisherPositionDTO publisherPositionDTO =
        createMinimalPublisherPositionDTO(siteType, placementCategory, videoSupport, platformType);
    assertFalse(
        validator.isValid(publisherPositionDTO, ctx),
        String.format(
            "Expected false when siteType: %s and placementCategory: %s and videoSupport: %s and platformType: %s",
            siteType, placementCategory, videoSupport, platformType));
    verifyValidationMessage(message);
    reset(ctx);
    initializeContext();
  }

  @Override
  public void initializeConstraint() {
    lenient()
        .when(placementDTOVideoSupportConstraint.message())
        .thenReturn("Invalid for specified site type, placementCategory and/or platform type");
    lenient()
        .when(placementDTOVideoSupportConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
    lenient().when(placementDTOVideoSupportConstraint.field()).thenReturn("videoSupport");
  }
}
