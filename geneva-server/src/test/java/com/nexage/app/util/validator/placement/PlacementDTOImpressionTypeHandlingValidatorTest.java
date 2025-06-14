package com.nexage.app.util.validator.placement;

import static com.nexage.admin.core.enums.PlacementCategory.INSTREAM_VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.enums.ImpressionTypeHandling;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementDTOImpressionTypeHandlingValidatorTest extends BaseValidatorTest {

  @Mock
  private PlacementDTOImpressionTypeHandlingConstraint placementDTOImpressionTypeHandlingConstraint;

  @InjectMocks private PlacementDTOImpressionTypeHandlingValidator validator;

  @Test
  void falseWhenSiteTypeNull() {
    assertFalse(
        validator.isValid(
            createDapPlacementDTO(
                null,
                INSTREAM_VIDEO,
                VIDEO_AND_BANNER,
                ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST),
            ctx));
    verifyValidationMessage(placementDTOImpressionTypeHandlingConstraint.emptyMessage());
  }

  @Test
  void falseWhenPlacementCategoryNull() {
    assertFalse(
        validator.isValid(
            createDapPlacementDTO(
                Type.MOBILE_WEB,
                null,
                VIDEO_AND_BANNER,
                ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST),
            ctx));
    verifyValidationMessage(placementDTOImpressionTypeHandlingConstraint.emptyMessage());
  }

  @Test
  void falseImpressionTypeHandlingNull() {
    assertFalse(
        validator.isValid(
            createDapPlacementDTO(Type.MOBILE_WEB, INSTREAM_VIDEO, VIDEO_AND_BANNER, null), ctx));
  }

  @Test
  void trueWhenImpressionTypeHandlingIsBasedOnInboundRequestAndIsDapPlacement() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(
            Type.DESKTOP,
            PlacementCategory.BANNER,
            VIDEO_AND_BANNER,
            ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  @Test
  void falseWhenImpressionTypeHandlingIsBasedOnInboundRequestAndIsNotDapPlacement() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(
            Type.DESKTOP,
            PlacementCategory.IN_ARTICLE,
            VIDEO_AND_BANNER,
            ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST);
    assertFalse(validator.isValid(placementDTO, ctx));
  }

  @Test
  void trueWhenImpressionTypeHandlingIsBasedOnPlacementConfig() {
    PlacementDTO placementDTO =
        createDapPlacementDTO(
            Type.DESKTOP,
            PlacementCategory.BANNER,
            VIDEO_AND_BANNER,
            ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG);
    assertTrue(validator.isValid(placementDTO, ctx));
  }

  private PlacementDTO createDapPlacementDTO(
      Type siteType,
      PlacementCategory placementCategory,
      VideoSupport videoSupport,
      ImpressionTypeHandling impressionTypeHandling) {
    SiteDTO site = new SiteDTO();
    site.setType(siteType);

    PlacementDTO position = new PlacementDTO();
    position.setSite(site);
    position.setPlacementCategory(placementCategory);
    position.setVideoSupport(videoSupport);
    position.setImpressionTypeHandling(impressionTypeHandling);
    return position;
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
