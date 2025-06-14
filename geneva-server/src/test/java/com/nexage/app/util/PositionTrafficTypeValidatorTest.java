package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PositionTrafficTypeValidatorTest {

  private final AdSourceValidator adSourceValidator = mock(AdSourceValidator.class);

  private PositionTrafficTypeValidator positionTrafficTypeValidator;

  @BeforeEach
  public void setUp() throws Exception {
    reset(adSourceValidator);
    positionTrafficTypeValidator = new PositionTrafficTypeValidator(adSourceValidator);
  }

  @Test
  void shouldDoNothing() {
    positionTrafficTypeValidator.validatePositionTiers(Collections.emptySet(), null, null);
    verify(adSourceValidator, never())
        .validateAdSourceAssignedToTiers(any(), any(), any(), anyBoolean());
  }

  @Test
  void shouldComplainInvalidInput() {
    var site = new Site();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                positionTrafficTypeValidator.validatePositionTrafficType(site, null, null, false));
    assertNotNull(exception);
    assertEquals(ServerErrorCodes.SERVER_INVALID_INPUT, exception.getErrorCode());
    verify(adSourceValidator, never())
        .validateAdSourceAssignedToTiers(any(), any(), any(), anyBoolean());
    var position = new Position();
    exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                positionTrafficTypeValidator.validatePositionTrafficType(
                    null, position, null, false));
    assertNotNull(exception);
    assertEquals(ServerErrorCodes.SERVER_INVALID_INPUT, exception.getErrorCode());
    verify(adSourceValidator, never())
        .validateAdSourceAssignedToTiers(any(), any(), any(), anyBoolean());
    var publisherTierDTO = new PublisherTierDTO();
    exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                positionTrafficTypeValidator.validatePositionTrafficType(
                    null, null, publisherTierDTO, false));
    assertNotNull(exception);
    assertEquals(ServerErrorCodes.SERVER_INVALID_INPUT, exception.getErrorCode());
    verify(adSourceValidator, never())
        .validateAdSourceAssignedToTiers(any(), any(), any(), anyBoolean());
  }

  @Test
  void shouldComplainTierTrafficInput() {
    var site = new Site();
    var position = new Position();
    position.setTrafficType(TrafficType.MEDIATION);
    var publisherTier = new PublisherTierDTO();
    publisherTier.setTierType(TierType.SUPER_AUCTION);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                positionTrafficTypeValidator.validatePositionTrafficType(
                    site, position, publisherTier, false));
    assertNotNull(exception);
    assertEquals(
        ServerErrorCodes.SERVER_TIER_TYPE_NOT_VALID_FOR_POSITION, exception.getErrorCode());

    position.setTrafficType(TrafficType.MEDIATION);
    publisherTier.setTierType(TierType.SY_DECISION_MAKER);
    exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                positionTrafficTypeValidator.validatePositionTrafficType(
                    site, position, publisherTier, false));
    assertNotNull(exception);
    assertEquals(
        ServerErrorCodes.SERVER_TIER_TYPE_NOT_VALID_FOR_POSITION, exception.getErrorCode());
    verify(adSourceValidator, never())
        .validateAdSourceAssignedToTiers(any(), any(), any(), anyBoolean());
  }

  @Test
  void shouldDoNothingRightParams() {
    var site = new Site();
    var position = new Position();
    position.setTrafficType(TrafficType.SMART_YIELD);
    var publisherTier = new PublisherTierDTO();
    publisherTier.setTierType(TierType.WATERFALL);
    positionTrafficTypeValidator.validatePositionTrafficType(site, position, publisherTier, false);
    verify(adSourceValidator, never())
        .validateAdSourceAssignedToTiers(any(), any(), any(), anyBoolean());
  }

  @Test
  void shouldValidateAdSource() {
    doNothing()
        .when(adSourceValidator)
        .validateAdSourceAssignedToTiers(any(), any(), any(), anyBoolean());
    var site = new Site();
    var position = new Position();
    position.setTrafficType(TrafficType.SMART_YIELD);
    var publisherTier = new PublisherTierDTO();
    publisherTier.setTierType(TierType.WATERFALL);
    var publisherTagDTO = new PublisherTagDTO();
    publisherTier.setTags(List.of(publisherTagDTO));
    positionTrafficTypeValidator.validatePositionTrafficType(site, position, publisherTier, false);
    verify(adSourceValidator, times(1))
        .validateAdSourceAssignedToTiers(any(), any(), any(), anyBoolean());
  }
}
