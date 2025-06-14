package com.nexage.admin.core.sparta.jpa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.model.HbPartnerPosition;
import com.nexage.admin.core.model.PlacementDooh;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.PositionMetrics;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PositionTest {

  private Position position;

  @BeforeEach
  void setUp() {
    position = new Position();
    position.setMetrics(Set.of(new PositionMetrics()));
    position.setHbPartnerPosition(Set.of(new HbPartnerPosition()));
    position.setVideoLinearity(VideoLinearity.LINEAR);
    position.setPlacementDooh(new PlacementDooh());
  }

  @Test
  void shouldNotShareReferenceWhenSetMetrics() {
    Set<PositionMetrics> metrics = Set.of(new PositionMetrics());
    position.setMetrics(metrics);
    assertNotSame(metrics, position.getMetrics());
  }

  @Test
  void shouldClearMetricsWhenMetricsNull() {
    position.setMetrics(null);
    assertTrue(position.getMetrics().isEmpty());
  }

  @Test
  void shouldNotShareReferenceWhenSetHbPartners() {
    Set<HbPartnerPosition> hbPartnerPositions = Set.of(new HbPartnerPosition());
    position.setHbPartnerPosition(hbPartnerPositions);
    assertNotSame(hbPartnerPositions, position.getHbPartnerPosition());
  }

  @Test
  void shouldSetNullHbPartnerPositionsWhenHbPartnerPositionsNull() {
    position.setHbPartnerPosition(null);
    assertTrue(position.getHbPartnerPosition().isEmpty());
  }

  @Test
  void shouldSetNullPlacementDoohWhenPlacementDoohNull() {
    position.setPlacementDooh(null);
    assertNull(position.getPlacementDooh());
  }

  @Test
  void shouldSetNullVideoAttributedWhenRemoveVideoIsCalled() {
    assertNotNull(position.getVideoLinearity());

    position.removeVideo();

    assertNull(position.getVideoLinearity());
  }

  @Test
  void testExternalAdVerificationSamplingRate() {
    assertNull(position.getExternalAdVerificationSamplingRate());

    position.setExternalAdVerificationSamplingRate(20.00f);

    assertEquals(20.00f, position.getExternalAdVerificationSamplingRate().floatValue());
  }

  @Test
  void shouldSetValidCreativeSuccessRateThreshold() {
    assertNull(position.getCreativeSuccessRateThreshold());
    position.setCreativeSuccessRateThreshold(new BigDecimal(20.00));
    assertEquals(20.00f, position.getCreativeSuccessRateThreshold().floatValue());
  }
}
