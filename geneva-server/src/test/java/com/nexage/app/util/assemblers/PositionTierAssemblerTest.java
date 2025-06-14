package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Tier;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PositionTierAssemblerTest {

  private PositionTierAssembler positionTierAssembler;

  @BeforeEach
  void setUp() {
    positionTierAssembler = new PositionTierAssembler();
  }

  @Test
  void shouldComplainTierNotFound() {
    final Long publisherTierPid = new Random().nextLong();
    PublisherTierDTO publisherTier = new PublisherTierDTO();
    publisherTier.setPid(publisherTierPid);
    Position position = new Position();
    Set<PublisherTierDTO> publisherTierDTOS = Set.of(publisherTier);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> positionTierAssembler.handleTiers(position, publisherTierDTOS));
    assertNotNull(exception);
    assertEquals(ServerErrorCodes.SERVER_TIER_NOT_FOUND_IN_POSITION, exception.getErrorCode());
  }

  @Test
  void shouldDoNothing() {
    Position position = new Position();
    positionTierAssembler.handleTiers(position, Collections.emptySet());
    assertNotNull(position);
    assertNotNull(position.getTiers());
    assertTrue(position.getTiers().isEmpty());
  }

  @Test
  void shouldMap() {
    PublisherTierDTO publisherTier = PublisherTierDTO.newBuilder().withPid(1L).withLevel(1).build();
    Tier tier = new Tier();
    tier.setPid(1L);
    Position position = new Position();
    position.setTiers(List.of(tier));
    Set<PublisherTierDTO> inputTiers = Set.of(publisherTier);
    positionTierAssembler.handleTiers(position, inputTiers);
    assertNotNull(position);
    assertNotNull(position.getTiers());
    assertFalse(position.getTiers().isEmpty());
    assertNotNull(position.getTiers().get(0));
    assertEquals(1L, position.getTiers().get(0).getPid().longValue());
  }
}
