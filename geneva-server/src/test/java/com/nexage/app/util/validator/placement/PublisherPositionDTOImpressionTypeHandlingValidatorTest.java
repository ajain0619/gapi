package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.enums.ImpressionTypeHandling;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoPlacementType;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.web.support.TestObjectsFactory;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PublisherPositionDTOImpressionTypeHandlingValidatorTest extends BaseValidatorTest {

  @Mock
  private PlacementDTOImpressionTypeHandlingConstraint placementDTOImpressionTypeHandlingConstraint;

  @InjectMocks private PublisherPositionDTOImpressionTypeHandlingValidator validator;

  @Test
  void falseWhenSiteTypeNull() {
    assertFalse(
        validator.isValid(
            createPublisherPositionDTO(
                null,
                INSTREAM_VIDEO,
                VIDEO_AND_BANNER,
                ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG),
            ctx));
    verifyValidationMessage(placementDTOImpressionTypeHandlingConstraint.emptyMessage());
  }

  @Test
  void falseWhenPlacementCategoryNull() {
    assertFalse(
        validator.isValid(
            createPublisherPositionDTO(
                PublisherSiteDTO.SiteType.MOBILE_WEB,
                null,
                VIDEO_AND_BANNER,
                ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG),
            ctx));
    verifyValidationMessage(placementDTOImpressionTypeHandlingConstraint.emptyMessage());
  }

  @Test
  void falseWhenImpressionTypeHandlingNull() {
    assertFalse(
        validator.isValid(
            createPublisherPositionDTO(
                PublisherSiteDTO.SiteType.MOBILE_WEB,
                PlacementCategory.BANNER,
                VIDEO_AND_BANNER,
                null),
            ctx));
  }

  @Test
  void trueWhenImpressionTypeHandlingIsBasedOnInboundRequestAndIsDapPlacement() {
    PublisherPositionDTO publisherPositionDTO =
        createPublisherPositionDTO(
            PublisherSiteDTO.SiteType.DESKTOP,
            PlacementCategory.BANNER,
            VIDEO_AND_BANNER,
            ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST);
    assertTrue(validator.isValid(publisherPositionDTO, ctx));
  }

  @Test
  void falseWhenImpressionTypeHandlingIsBasedOnInboundRequestAndIsNonDapPlacement() {
    PublisherPositionDTO publisherPositionDTO =
        createPublisherPositionDTO(
            PublisherSiteDTO.SiteType.DESKTOP,
            PlacementCategory.IN_ARTICLE,
            VIDEO_AND_BANNER,
            ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST);
    assertFalse(validator.isValid(publisherPositionDTO, ctx));
  }

  @Test
  void trueWhenImpressionTypeHandlingIsBasedOnPlacementConfig() {
    PublisherPositionDTO publisherPositionDTO =
        createPublisherPositionDTO(
            PublisherSiteDTO.SiteType.DESKTOP,
            PlacementCategory.BANNER,
            VIDEO_AND_BANNER,
            ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG);
    assertTrue(validator.isValid(publisherPositionDTO, ctx));
  }

  private PublisherPositionDTO createPublisherPositionDTO(
      PublisherSiteDTO.SiteType siteType,
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      ImpressionTypeHandling impressionTypeHandling) {

    PublisherSiteDTO site = new PublisherSiteDTO();
    site.setType(siteType);

    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setSite(site);
    publisherPositionDTO.setPlacementCategory(placementCategory);
    publisherPositionDTO.setVideoSupport(videoSupport);
    publisherPositionDTO.setImpressionTypeHandling(impressionTypeHandling);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPlayerHeight(null);
    placementVideoDTO.setPlayerWidth(null);
    placementVideoDTO.setVideoPlacementType(VideoPlacementType.IN_ARTICLE);
    publisherPositionDTO.setPlacementVideo(placementVideoDTO);
    return publisherPositionDTO;
  }

  private void verifyValidationMessage(String expectedMessage) {
    verify(ctx).buildConstraintViolationWithTemplate(expectedMessage);
  }

  @Override
  public void initializeConstraint() {
    lenient()
        .when(placementDTOImpressionTypeHandlingConstraint.message())
        .thenReturn(ValidationMessages.WRONG_VALUE);
    lenient()
        .when(placementDTOImpressionTypeHandlingConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
    lenient()
        .when(placementDTOImpressionTypeHandlingConstraint.field())
        .thenReturn("placementCategory");
  }
}
