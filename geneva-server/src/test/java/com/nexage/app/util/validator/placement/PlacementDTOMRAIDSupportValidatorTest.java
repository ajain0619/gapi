package com.nexage.app.util.validator.placement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;

import com.nexage.admin.core.enums.MRAIDSupport;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.ValidationMessages;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PlacementDTOMRAIDSupportValidatorTest extends BaseValidatorTest {

  @Mock private PlacementDTOMRAIDSupportConstraint placementDTOMRAIDSupportConstraint;
  @InjectMocks private PlacementDTOMRAIDSupportValidator validator;

  @Test
  void validYesMRAIDSupport() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidSupport(MRAIDSupport.YES);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void nullPlacementCategory() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidSupport(MRAIDSupport.YES);
    boolean valid = validator.isValid(placementDTO, ctx);
    assertFalse(valid);
  }

  @Test
  void validNullMRAIDSupport() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidSupport(null);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    boolean valid = validator.isValid(placementDTO, ctx);
    assertTrue(valid);
  }

  @Test
  void validNoMRAIDSupportInArticle() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidSupport(MRAIDSupport.NO);
    placementDTO.setPlacementCategory(PlacementCategory.IN_ARTICLE);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void validNoMRAIDSupportInFeed() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidSupport(MRAIDSupport.NO);
    placementDTO.setPlacementCategory(PlacementCategory.IN_FEED);
    boolean valid = validator.isValid(placementDTO, ctx);
    verifyNoInteractions(ctx);
    assertTrue(valid);
  }

  @Test
  void invalidYesMRAIDSupportInArticle() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidSupport(MRAIDSupport.YES);
    placementDTO.setPlacementCategory(PlacementCategory.IN_ARTICLE);
    boolean valid = validator.isValid(placementDTO, ctx);
    assertFalse(valid);
  }

  @Test
  void invalidYesMRAIDSupportInFeed() {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setMraidSupport(MRAIDSupport.YES);
    placementDTO.setPlacementCategory(PlacementCategory.IN_FEED);
    boolean valid = validator.isValid(placementDTO, ctx);
    assertFalse(valid);
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
