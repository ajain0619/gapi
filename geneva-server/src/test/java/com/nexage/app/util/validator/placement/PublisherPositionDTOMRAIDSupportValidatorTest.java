package com.nexage.app.util.validator.placement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;

import com.nexage.admin.core.enums.MRAIDSupport;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PublisherPositionDTOMRAIDSupportValidatorTest extends BaseValidatorTest {

  @Mock private PlacementDTOMRAIDSupportConstraint placementDTOMRAIDSupportConstraint;
  @InjectMocks private PublisherPositionDTOMRAIDSupportValidator validator;

  @Test
  void nullPlacementCategory() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setMraidSupport(MRAIDSupport.YES);
    boolean valid = validator.isValid(publisherPositionDTO, ctx);
    assertFalse(valid);
  }

  @Test
  void validNullMRAIDSupport() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setMraidSupport(null);
    publisherPositionDTO.setPlacementCategory(PlacementCategory.IN_ARTICLE);
    boolean valid = validator.isValid(publisherPositionDTO, ctx);
    assertTrue(valid);
  }

  @Test
  void validNoMRAIDSupportInArticle() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setMraidSupport(MRAIDSupport.NO);
    publisherPositionDTO.setPlacementCategory(PlacementCategory.IN_ARTICLE);
    boolean valid = validator.isValid(publisherPositionDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validNoMRAIDSupportInFeed() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setMraidSupport(MRAIDSupport.NO);
    publisherPositionDTO.setPlacementCategory(PlacementCategory.IN_FEED);
    boolean valid = validator.isValid(publisherPositionDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void invalidYesMRAIDSupportInArticle() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setMraidSupport(MRAIDSupport.YES);
    publisherPositionDTO.setPlacementCategory(PlacementCategory.IN_ARTICLE);
    boolean valid = validator.isValid(publisherPositionDTO, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidYesMRAIDSupportInFeed() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setMraidSupport(MRAIDSupport.YES);
    publisherPositionDTO.setPlacementCategory(PlacementCategory.IN_FEED);
    boolean valid = validator.isValid(publisherPositionDTO, ctx);
    assertFalse(valid);
  }

  @Test
  void validNoMRAIDSupportInStream() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setMraidSupport(MRAIDSupport.NO);
    publisherPositionDTO.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    boolean valid = validator.isValid(publisherPositionDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validYesMRAIDSupportInStream() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setMraidSupport(MRAIDSupport.YES);
    publisherPositionDTO.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    boolean valid = validator.isValid(publisherPositionDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Override
  public void initializeConstraint() {
    lenient()
        .when(placementDTOMRAIDSupportConstraint.message())
        .thenReturn("Placement MRAID Support is invalid");
    lenient().when(placementDTOMRAIDSupportConstraint.field()).thenReturn("mraidSupport");
    lenient()
        .when(placementDTOMRAIDSupportConstraint.emptyMessage())
        .thenReturn(ValidationMessages.WRONG_IS_EMPTY);
  }
}
