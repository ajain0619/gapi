package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.ImpressionTypeHandling;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoSupport;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PositionTest {

  private Position position;

  @BeforeEach
  void setupPosition() {
    position = new Position();
    position.setPid(1L);
    position.setName("postion");
  }

  @Test
  void shouldReturnPositionToString() {
    assertEquals(ToStringBuilder.reflectionToString(position), position.toString());
  }

  @Test
  void shoudldReturnNewTier() {
    position.setTiers(List.of(new Tier(0)));
    position.newTier();
    assertNotNull(position.getTier(1));
  }

  @Test
  void shouldRenumberTiers() {
    position.setTiers(List.of(new Tier(10), new Tier(11)));
    position.renumberTiers();
    assertNotNull(position.getTier(0));
    assertNotNull(position.getTier(1));
  }

  @Test
  void shouldSetVideoSupport() {
    position.setVideoSupport(VideoSupport.VIDEO);
    assertEquals(VideoSupport.VIDEO, position.getVideoSupport());
  }

  @Test
  void shouldReturnLinearVideoLinearity() {
    position.setVideoSupport(VideoSupport.VIDEO);
    assertEquals(VideoLinearity.LINEAR, position.getVideoLinearity());
  }

  @Test
  void shouldSetImpressionTypeHandling() {
    position.setImpressionTypeHandling(ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST);
    assertEquals(
        ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST, position.getImpressionTypeHandling());
  }

  @Test
  void shouldSetAndGetLongform() {
    position.setLongform(true);
    assertTrue(position.getLongform());

    position.setLongform(false);
    assertFalse(position.getLongform());
  }
}
