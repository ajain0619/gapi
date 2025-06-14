package com.nexage.admin.core.sparta.jpa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.PlacementVideoLinearity;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.repository.PlacementVideoRepository;
import com.nexage.admin.core.repository.PositionViewRepository;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/position-repository.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class PlacementVideoIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private PositionViewRepository positionViewRepository;
  @Autowired private PlacementVideoRepository placementVideoRepository;

  private static final Long DEFAULT_PID = 2L;
  private static final PlacementVideoLinearity LINEARITY = PlacementVideoLinearity.LINEAR;
  private static final Boolean PLAYER_REQUIRED = false;
  private static final String testPlayerBrand = "test_player";

  @Test
  void shouldTestWhenPlacementVideoIsNull() {
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    placementVideo.setPosition(null);
    assertThrows(
        JpaSystemException.class, () -> placementVideoRepository.saveAndFlush(placementVideo));
  }

  @Test
  void shouldTestWhenPlacementVideoIsInvalid() {
    Long invalidPositionPid = 1000L;
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    PositionView position =
        positionViewRepository.findById(invalidPositionPid).stream().findFirst().orElse(null);
    placementVideo.setPosition(position);
    assertThrows(
        JpaSystemException.class, () -> placementVideoRepository.saveAndFlush(placementVideo));
  }

  @Test
  void shouldTestWhenPositionIsValid() {
    Long validPositionPid = DEFAULT_PID;
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    PlacementVideo savedPlacementVideo = placementVideoRepository.saveAndFlush(placementVideo);
    assertEquals(validPositionPid, savedPlacementVideo.getPosition().getPid());
  }

  @Test
  void shouldTestWhenVastVersionIsNull() {
    // Null
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    placementVideo.setVastVersion(null);
    PlacementVideo savedPlacementVideo = placementVideoRepository.saveAndFlush(placementVideo);
    assertNull(savedPlacementVideo.getVastVersion());
  }

  @Test
  void shouldTestWhenFileFormatsIsNull() {
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    placementVideo.setFileFormats(null);
    PlacementVideo savedPlacementVideo = placementVideoRepository.saveAndFlush(placementVideo);
    assertNull(savedPlacementVideo.getFileFormats());
  }

  @Test
  void shouldTestWithDefaultValues() {
    PlacementVideo placementVideo = createDefaultPlacementVideo();

    PlacementVideo savedPlacementVideo = placementVideoRepository.saveAndFlush(placementVideo);
    assertEquals(LINEARITY, savedPlacementVideo.getLinearity());
    assertNull(savedPlacementVideo.getPlayerHeight());
    assertNull(savedPlacementVideo.getPlayerWidth());
    assertFalse(savedPlacementVideo.isLongform());
    assertNull(savedPlacementVideo.getStreamType());
    assertNull(savedPlacementVideo.getPlayerBrand());
    assertNull(savedPlacementVideo.getSsai());
    assertFalse(savedPlacementVideo.isMultiImpressionBid());
    assertFalse(savedPlacementVideo.isCompetitiveSeparation());
  }

  @Test
  void shouldTestWhenOtherValues() {
    Integer playerHeight = 320;
    Integer playerWidth = 480;
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    placementVideo.setLinearity(PlacementVideoLinearity.NON_LINEAR);
    placementVideo.setPlayerRequired(true);
    placementVideo.setPlayerHeight(playerHeight);
    placementVideo.setPlayerWidth(playerWidth);
    placementVideo.setLongform(true);
    placementVideo.setStreamType(PlacementVideoStreamType.VOD);
    placementVideo.setPlayerBrand(testPlayerBrand);
    placementVideo.setSsai(PlacementVideoSsai.ALL_CLIENT_SIDE);
    placementVideo.setMultiImpressionBid(true);
    placementVideo.setCompetitiveSeparation(true);

    PlacementVideo savedPlacementVideo = placementVideoRepository.saveAndFlush(placementVideo);
    assertEquals(PlacementVideoLinearity.NON_LINEAR, savedPlacementVideo.getLinearity());
    assertTrue(savedPlacementVideo.isPlayerRequired());
    assertEquals(playerHeight, savedPlacementVideo.getPlayerHeight());
    assertEquals(playerWidth, savedPlacementVideo.getPlayerWidth());
    assertTrue(savedPlacementVideo.isLongform());
    assertEquals(PlacementVideoStreamType.VOD, savedPlacementVideo.getStreamType());
    assertEquals(testPlayerBrand, savedPlacementVideo.getPlayerBrand());
    assertEquals(PlacementVideoSsai.ALL_CLIENT_SIDE, savedPlacementVideo.getSsai());
    assertTrue(savedPlacementVideo.isMultiImpressionBid());
    assertTrue(savedPlacementVideo.isCompetitiveSeparation());
  }

  @Test
  void shouldTestWhenPlayerHeightAndWidthAreNegative() {
    Integer playerWidth = -1;
    Integer playerHeight = -1;
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    placementVideo.setPlayerWidth(playerWidth);
    placementVideo.setPlayerHeight(playerHeight);
    assertThrows(
        ConstraintViolationException.class,
        () -> placementVideoRepository.saveAndFlush(placementVideo));
  }

  @Test
  void shouldTestWhenPlayerHeightAndWidthAreZero() {
    Integer playerWidth = 0;
    Integer playerHeight = 0;
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    placementVideo.setPlayerWidth(playerWidth);
    placementVideo.setPlayerHeight(playerHeight);
    assertThrows(
        ConstraintViolationException.class,
        () -> placementVideoRepository.saveAndFlush(placementVideo));
  }

  @Test
  void shouldTestWhenPlayerHeightAndWidthAreAbove9999() {
    Integer playerWidth = 10000;
    Integer playerHeight = 10000;
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    placementVideo.setPlayerWidth(playerWidth);
    placementVideo.setPlayerHeight(playerHeight);
    assertThrows(
        ConstraintViolationException.class,
        () -> placementVideoRepository.saveAndFlush(placementVideo));
  }

  @Test
  void shouldTestOneCompanion() {
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    placementVideo.addCompanion(placementVideoCompanion);

    PlacementVideo savedPlacementVideo = placementVideoRepository.saveAndFlush(placementVideo);
    assertNotNull(savedPlacementVideo);
    assertEquals(1, savedPlacementVideo.getCompanions().size());
  }

  @Test
  void shouldTestMultipleCompanions() {
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    placementVideo.addCompanion(placementVideoCompanion);

    PlacementVideoCompanion placementVideoCompanion2 = createDefaultPlacementVideoCompanion();
    placementVideo.addCompanion(placementVideoCompanion2);

    PlacementVideo savedPlacementVideo = placementVideoRepository.saveAndFlush(placementVideo);
    assertNotNull(savedPlacementVideo);
    assertEquals(2, savedPlacementVideo.getCompanions().size());
  }

  @Test
  void shouldTestMultipleCompanionsDelete() {
    PlacementVideo placementVideo = createDefaultPlacementVideo();
    PlacementVideoCompanion placementVideoCompanion = createDefaultPlacementVideoCompanion();
    placementVideo.addCompanion(placementVideoCompanion);

    PlacementVideoCompanion placementVideoCompanion2 = createDefaultPlacementVideoCompanion();
    placementVideo.addCompanion(placementVideoCompanion2);

    PlacementVideo savedPlacementVideo = placementVideoRepository.saveAndFlush(placementVideo);
    assertNotNull(savedPlacementVideo);
    assertEquals(2, savedPlacementVideo.getCompanions().size());

    placementVideo.removeCompanion(placementVideoCompanion2);
    assertEquals(1, savedPlacementVideo.getCompanions().size());
  }

  private PlacementVideo createDefaultPlacementVideo() {
    Long positionPid = DEFAULT_PID;
    PlacementVideo placementVideo = new PlacementVideo();

    PositionView position =
        positionViewRepository.findById(positionPid).stream().findFirst().orElse(null);
    placementVideo.setPosition(position);
    placementVideo.setLinearity(LINEARITY);
    placementVideo.setPlayerRequired(PLAYER_REQUIRED);

    return placementVideo;
  }

  private PlacementVideoCompanion createDefaultPlacementVideoCompanion() {
    PlacementVideoCompanion placementVideoCompanion = new PlacementVideoCompanion();
    placementVideoCompanion.setHeight(320);
    placementVideoCompanion.setWidth(480);
    return placementVideoCompanion;
  }
}
