package com.nexage.app.util.validator.placement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.NonNullDoohConstraint;
import com.nexage.app.util.validator.PublisherPositionDTONonNullDoohValidator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PublisherPositionDTONonNullDoohValidatorTest extends BaseValidatorTest {

  @Mock private NonNullDoohConstraint constraint;
  @InjectMocks private PublisherPositionDTONonNullDoohValidator validator;

  private final String message = "Dooh nonnull message";

  @Test
  void shouldReturnFalseWhenSiteTypeIsDoohAndNullDoohObject() {
    assertFalse(
        validator.isValid(
            PublisherPositionDTO.builder()
                .withSite(PublisherSiteDTO.newBuilder().withType(SiteType.DOOH).build())
                .build(),
            ctx));
  }

  @Test
  void shouldReturnTrueIfSiteIsNull() {
    assertTrue(validator.isValid(PublisherPositionDTO.builder().build(), ctx));
  }

  @Test
  void shouldReturnTrueWhenSiteTypeNotDooh() {
    assertTrue(
        validator.isValid(
            PublisherPositionDTO.builder()
                .withSite(PublisherSiteDTO.newBuilder().withType(SiteType.DESKTOP).build())
                .build(),
            ctx));
  }

  @Override
  protected void initializeConstraint() {
    lenient().when(constraint.message()).thenReturn(message);
  }
}
