package com.nexage.app.util.validator.placement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.DoohConstraint;
import com.nexage.app.util.validator.PublisherPositionDTODoohValidator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PublisherPositionDTODoohValidatorTest extends BaseValidatorTest {

  @Mock private DoohConstraint constraint;

  @InjectMocks private PublisherPositionDTODoohValidator publisherPositionDTODoohValidator;
  private static String message = "test message";

  @Test
  void shouldReturnTrueIfSiteNull() {
    assertTrue(
        publisherPositionDTODoohValidator.isValid(PublisherPositionDTO.builder().build(), ctx));
  }

  @Test
  void shouldReturnTrueWhenValidDoohPosition() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();

    assertTrue(publisherPositionDTODoohValidator.isValid(publisherPositionDTO, ctx));
  }

  @Test
  void shouldReturnFalseWhenInvalid() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    PublisherSiteDTO doohSite = new PublisherSiteDTO();
    doohSite.setType(SiteType.DESKTOP);
    publisherPositionDTO.setSite(doohSite);
    publisherPositionDTO.setDooh(new PlacementDoohDTO());

    assertFalse(publisherPositionDTODoohValidator.isValid(publisherPositionDTO, ctx));
  }

  @Test
  void shouldReturnTrueWhenValidDoohAndSite() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setSite(new PublisherSiteDTO());
    publisherPositionDTO.getSite().setType(SiteType.DOOH);
    publisherPositionDTO.setDooh(new PlacementDoohDTO());

    assertTrue(publisherPositionDTODoohValidator.isValid(publisherPositionDTO, ctx));
  }

  @Test
  void shouldReturnTrueWhenSiteTypeNullAndDoohNotNull() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setSite(new PublisherSiteDTO());
    publisherPositionDTO.setDooh(new PlacementDoohDTO());

    assertTrue(publisherPositionDTODoohValidator.isValid(publisherPositionDTO, ctx));
  }

  @Override
  protected void initializeConstraint() {
    lenient().when(constraint.message()).thenReturn(message);
  }
}
