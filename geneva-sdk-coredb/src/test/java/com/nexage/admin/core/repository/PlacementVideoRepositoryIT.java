package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.enums.PlacementVideoVastVersion;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoCompanion;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/placement-video-companion-repository.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class PlacementVideoRepositoryIT extends CoreDbSdkIntegrationTestBase {
  @Autowired private PositionViewRepository positionViewRepository;
  @Autowired private PlacementVideoRepository placementVideoRepository;
  @Autowired private PlacementVideoCompanionRepository placementVideoCompanionRepository;
  private static final String PLAYER_BRAND = "test_player";
  private static final String PLAYER_BRAND_UPDATED = "test_player_updated";

  @ParameterizedTest
  @CsvSource(
      value = {
        "1,0", // No companion
        "3,1", // One companion
        "4,2" // Multiple companions
      })
  void shouldTestGetPositionVideoWithDifferentCompanion(Long id, int expectedCompanionSize) {
    Optional<PlacementVideo> placementVideo = placementVideoRepository.findById(1L);
    assertTrue(placementVideo.isPresent());
    assertEquals(0, placementVideo.get().getCompanions().size());
  }

  @Test
  void shouldTestGetPositionVideoByPositionPid() {
    // invalid position pid
    Long invalidPositionPid = 5L;
    Optional<PlacementVideo> placementVideo = placementVideoRepository.findById(invalidPositionPid);
    assertTrue(placementVideo.isEmpty());

    // valid position with valid position video
    Long validPositionPid = 1L;
    placementVideo = placementVideoRepository.findById(validPositionPid);
    assertTrue(placementVideo.isPresent());

    // valid position pid and no position video
    Long validPositionPidWithNoPositionVideo = 2L;
    placementVideo = placementVideoRepository.findById(validPositionPidWithNoPositionVideo);
    assertTrue(placementVideo.isEmpty());
  }

  @Test
  void shouldTestUpdatePositionVideoWithPid() {
    Long pid = 3L;
    PlacementVideo placementVideo = placementVideoRepository.findById(pid).get();
    assertEquals(PlacementVideoVastVersion.VAST2_0, placementVideo.getVastVersion());
    assertFalse(placementVideo.isLongform());
    assertNull(placementVideo.getStreamType());
    assertNull(placementVideo.getSsai());
    assertNull(placementVideo.getPlayerBrand());
    assertFalse(placementVideo.isMultiImpressionBid());
    assertFalse(placementVideo.isCompetitiveSeparation());

    placementVideo.setVastVersion(PlacementVideoVastVersion.VAST3_0);
    placementVideo.setLongform(true);
    placementVideo.setStreamType(PlacementVideoStreamType.LIVE);
    placementVideo.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
    placementVideo.setMultiImpressionBid(true);
    placementVideo.setCompetitiveSeparation(true);
    placementVideo.setPosition(
        positionViewRepository.findById(pid).stream().findFirst().orElse(null));
    PlacementVideo savedPlacementVideo = placementVideoRepository.save(placementVideo);
    assertEquals(PlacementVideoVastVersion.VAST3_0, savedPlacementVideo.getVastVersion());
    assertTrue(savedPlacementVideo.isLongform());
    assertEquals(PlacementVideoStreamType.LIVE, savedPlacementVideo.getStreamType());
    assertEquals(PlacementVideoSsai.ALL_SERVER_SIDE, savedPlacementVideo.getSsai());
    assertNull(savedPlacementVideo.getPlayerBrand());
    assertTrue(savedPlacementVideo.isMultiImpressionBid());
    assertTrue(savedPlacementVideo.isCompetitiveSeparation());
  }

  @Test
  void shouldTestUpdatePositionVideoLongformWithPid() {
    Long pid = 4L;
    PlacementVideo placementVideo = placementVideoRepository.findById(pid).get();
    assertTrue(placementVideo.isLongform());
    assertEquals(PlacementVideoStreamType.VOD, placementVideo.getStreamType());
    assertEquals(PlacementVideoSsai.ASSETS_STICHED_SERVER_SIDE, placementVideo.getSsai());
    assertEquals(PLAYER_BRAND, placementVideo.getPlayerBrand());

    placementVideo.setStreamType(PlacementVideoStreamType.LIVE);
    placementVideo.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
    placementVideo.setPlayerBrand(PLAYER_BRAND_UPDATED);
    placementVideo.setPosition(
        positionViewRepository.findById(pid).stream().findFirst().orElse(null));
    PlacementVideo savedPlacementVideo = placementVideoRepository.save(placementVideo);
    assertTrue(savedPlacementVideo.isLongform());
    assertEquals(PlacementVideoStreamType.LIVE, savedPlacementVideo.getStreamType());
    assertEquals(PlacementVideoSsai.ALL_SERVER_SIDE, savedPlacementVideo.getSsai());
    assertEquals(PLAYER_BRAND_UPDATED, savedPlacementVideo.getPlayerBrand());
  }

  @Test
  void shouldTestDeletePositionVideoAndCompanionWithPid() {
    Long positionVideoPid = 4L;
    Long positionVideoCompanionPid = 11L;
    Optional<PlacementVideo> placementVideo = placementVideoRepository.findById(positionVideoPid);
    Optional<PlacementVideoCompanion> placementVideoCompanion =
        placementVideoCompanionRepository.findById(positionVideoCompanionPid);
    assertTrue(placementVideo.isPresent());
    assertTrue(placementVideoCompanion.isPresent());

    placementVideoRepository.deleteById(positionVideoPid);
    placementVideo = placementVideoRepository.findById(positionVideoPid);
    placementVideoCompanion = placementVideoCompanionRepository.findById(positionVideoCompanionPid);
    assertTrue(placementVideo.isEmpty());
    assertTrue(placementVideoCompanion.isEmpty());
  }

  @Test
  void shouldTestExistsByPid() {
    assertTrue(placementVideoRepository.existsByPid(1L));
    assertFalse(placementVideoRepository.existsByPid(2L));
    assertTrue(placementVideoRepository.existsByPid(3L));
    assertFalse(placementVideoRepository.existsByPid(5L));
  }

  @Test
  void shouldUpdatePositionVideoCorrectlyWithMultiBiddingFields() {
    Long pid = 4L;
    PlacementVideo placementVideo = placementVideoRepository.findById(pid).get();
    assertTrue(placementVideo.isMultiImpressionBid());
    assertFalse(placementVideo.isCompetitiveSeparation());

    placementVideo.setMultiImpressionBid(false);
    placementVideo.setCompetitiveSeparation(false);
    placementVideo.setPosition(
        positionViewRepository.findById(pid).stream().findFirst().orElse(null));
    PlacementVideo savedPlacementVideo = placementVideoRepository.save(placementVideo);
    assertFalse(savedPlacementVideo.isMultiImpressionBid());
    assertFalse(savedPlacementVideo.isCompetitiveSeparation());
  }
}
