package com.nexage.app.mapper.site;

import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.MRAIDSupport;
import com.nexage.admin.core.enums.MediaType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.PlacementVideoLinearity;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoCompanion;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementVideoCompanionDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.PlacementVideoPlaylistDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.util.placement.DapVideoPlacementUtil;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Test;

class PlacementVideoDTOMapperTest {

  private Long DEFAULT_PID = 123L;

  @Test
  void shouldMapPlacementVideoDTOToPlacementVideo() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    Long placementVideoPid = RandomUtils.nextLong();
    placementVideoDTO.setPid(placementVideoPid);
    placementVideoDTO.setLinearity(PlacementVideoLinearity.LINEAR);
    placementVideoDTO.setPlayerRequired(true);
    placementVideoDTO.setPlayerHeight(320);
    placementVideoDTO.setPlayerWidth(480);
    placementVideoDTO.setLongform(true);
    placementVideoDTO.setStreamType(PlacementVideoStreamType.LIVE);
    placementVideoDTO.setPlayerBrand("test_player");
    placementVideoDTO.setSsai(PlacementVideoSsai.ASSETS_STICHED_SERVER_SIDE);

    PlacementVideoPlaylistDTO placementVideoPlaylistDTO =
        TestObjectsFactory.createDefaultPlacementVideoPlaylistDTO(placementVideoPid);

    placementVideoDTO.setPlaylistInfo(Arrays.asList(placementVideoPlaylistDTO));

    PlacementVideo placementVideo = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);
    assertNotNull(placementVideo);
    assertEquals(placementVideoPid, placementVideo.getPid());
    assertEquals(PlacementVideoLinearity.LINEAR, placementVideo.getLinearity());
    assertTrue(placementVideo.isPlayerRequired());
    assertEquals((Integer) 320, placementVideo.getPlayerHeight());
    assertEquals((Integer) 480, placementVideo.getPlayerWidth());
    assertTrue(placementVideoDTO.isLongform());
    assertEquals(PlacementVideoStreamType.LIVE, placementVideoDTO.getStreamType());
    assertEquals("test_player", placementVideoDTO.getPlayerBrand());
    assertEquals(PlacementVideoSsai.ASSETS_STICHED_SERVER_SIDE, placementVideoDTO.getSsai());
    assertEquals(
        placementVideoPid, placementVideoDTO.getPlaylistInfo().get(0).getPlacementVideoPid());
    assertEquals("someurl.mp4", placementVideoDTO.getPlaylistInfo().get(0).getFallbackURL());
    assertEquals(MediaType.VIDEO_MP4, placementVideoDTO.getPlaylistInfo().get(0).getMediaType());
  }

  @Test
  void shouldMapPlacementVideoDTOToPlacementVideoMultiBiddingParams() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    Long placementVideoPid = RandomUtils.nextLong();
    placementVideoDTO.setPid(placementVideoPid);
    placementVideoDTO.setMultiImpressionBid(true);
    placementVideoDTO.setCompetitiveSeparation(false);
    PlacementVideo placementVideo = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);
    assertNotNull(placementVideo);
    assertEquals(placementVideoPid, placementVideo.getPid());
    assertTrue(placementVideoDTO.isMultiImpressionBid());
    assertFalse(placementVideo.isCompetitiveSeparation());
  }

  @Test
  void shouldMapPlacementVideoToPlacementVideoDTO() {
    PlacementVideo placementVideo = new PlacementVideo();
    Long placementVideoPid = RandomUtils.nextLong();
    placementVideo.setPid(placementVideoPid);
    placementVideo.setLinearity(PlacementVideoLinearity.LINEAR);
    placementVideo.setPlayerRequired(true);
    placementVideo.setPlayerHeight(320);
    placementVideo.setPlayerWidth(480);
    placementVideo.setLongform(true);
    placementVideo.setStreamType(PlacementVideoStreamType.VOD);
    placementVideo.setPlayerBrand("test_player");
    placementVideo.setSsai(PlacementVideoSsai.ALL_CLIENT_SIDE);
    placementVideo.setMultiImpressionBid(true);
    placementVideo.setCompetitiveSeparation(true);

    PlacementVideoDTO placementVideoDTO = PlacementVideoDTOMapper.MAPPER.map(placementVideo);
    assertNotNull(placementVideoDTO);
    assertEquals(placementVideoPid, placementVideoDTO.getPid());
    assertEquals(PlacementVideoLinearity.LINEAR, placementVideoDTO.getLinearity());
    assertTrue(placementVideoDTO.isPlayerRequired());
    assertEquals((Integer) 320, placementVideoDTO.getPlayerHeight());
    assertEquals((Integer) 480, placementVideoDTO.getPlayerWidth());
    assertTrue(placementVideoDTO.isLongform());
    assertEquals(PlacementVideoStreamType.VOD, placementVideoDTO.getStreamType());
    assertEquals("test_player", placementVideoDTO.getPlayerBrand());
    assertEquals(PlacementVideoSsai.ALL_CLIENT_SIDE, placementVideoDTO.getSsai());
    assertTrue(placementVideoDTO.isMultiImpressionBid());
    assertTrue(placementVideoDTO.isCompetitiveSeparation());
  }

  @Test
  void shouldMapPlacementVideoCompanionDTOWhenMappingPlacementVideoDTO() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLinearity(null);
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    List<PlacementVideoCompanionDTO> placementVideoCompanionDTOList =
        new ArrayList<PlacementVideoCompanionDTO>();
    placementVideoCompanionDTOList.add(placementVideoCompanionDTO);
    placementVideoDTO.setCompanions(placementVideoCompanionDTOList);

    PlacementVideo placementVideo = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);
    List<PlacementVideoCompanion> placementVideoCompanionList = placementVideo.getCompanions();
    assertNotNull(placementVideoCompanionList);
    assertEquals(1, placementVideoCompanionList.size());
    assertNotNull(placementVideo.getLinearity());
    assertEquals(PlacementVideoLinearity.LINEAR, placementVideo.getLinearity());

    PlacementVideoCompanionDTO placementVideoCompanionDTO2 =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoCompanionDTOList.add(placementVideoCompanionDTO2);
    placementVideoDTO.setCompanions(placementVideoCompanionDTOList);

    placementVideo = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);
    placementVideoCompanionList = placementVideo.getCompanions();
    assertNotNull(placementVideoCompanionList);
    assertEquals(2, placementVideoCompanionList.size());
  }

  @Test
  void shouldMapPlacementVideoCompanionWhenMappingPlacementVideo() {
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    PlacementVideoCompanion placementVideoCompanion =
        TestObjectsFactory.createDefaultPlacementVideoCompanion();
    List<PlacementVideoCompanion> placementVideoCompanionList = new ArrayList<>();
    placementVideoCompanionList.add(placementVideoCompanion);
    placementVideo.setCompanions(placementVideoCompanionList);

    PlacementVideoDTO placementVideoDTO = PlacementVideoDTOMapper.MAPPER.map(placementVideo);
    List<PlacementVideoCompanionDTO> placementVideoCompanionDTOList =
        placementVideoDTO.getCompanions();
    assertNotNull(placementVideoCompanionDTOList);
    assertEquals(1, placementVideoCompanionDTOList.size());

    PlacementVideoCompanion placementVideoCompanion2 =
        TestObjectsFactory.createDefaultPlacementVideoCompanion();
    placementVideoCompanionList.add(placementVideoCompanion2);
    placementVideo.setCompanions(placementVideoCompanionList);

    placementVideoDTO = PlacementVideoDTOMapper.MAPPER.map(placementVideo);
    placementVideoCompanionDTOList = placementVideoDTO.getCompanions();
    assertNotNull(placementVideoCompanionDTOList);
    assertEquals(2, placementVideoCompanionDTOList.size());
  }

  @Test
  @SneakyThrows
  void shouldFetchPlacementVideoDTOFromPlacemntDTO() {
    VideoLinearity LINEARITY = VideoLinearity.LINEAR;
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setVideoLinearity(LINEARITY);

    PlacementVideoDTO placementVideoDTO =
        PlacementVideoDTOMapper.MAPPER.populatePlacementVideoDTOFromPosition(placementDTO);

    assertNotNull(placementVideoDTO);
    assertEquals(
        placementDTO.getVideoLinearity().toString(), placementVideoDTO.getLinearity().toString());
    // default false
    assertFalse(placementVideoDTO.isLongform());
    assertFalse(placementVideoDTO.isMultiImpressionBid());
    assertFalse(placementVideoDTO.isCompetitiveSeparation());
  }

  @Test
  @SneakyThrows
  void shouldFetchPlacementVideoDTOFromPublisherPositionDTO() {
    VideoLinearity LINEARITY = VideoLinearity.LINEAR;
    PublisherPositionDTO publisherPositionDTO = createPublisherPositionDTO();
    publisherPositionDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPositionDTO.setVideoLinearity(LINEARITY);

    PlacementVideoDTO placementVideoDTO =
        PlacementVideoDTOMapper.MAPPER.populatePlacementVideoDTOFromPosition(publisherPositionDTO);

    assertNotNull(placementVideoDTO);
    assertEquals(
        publisherPositionDTO.getVideoLinearity().toString(),
        placementVideoDTO.getLinearity().toString());

    assertFalse(placementVideoDTO.isLongform());
    assertNull(placementVideoDTO.getStreamType());
    assertNull(placementVideoDTO.getPlayerBrand());
    assertNull(placementVideoDTO.getSsai());
    assertFalse(placementVideoDTO.isMultiImpressionBid());
    assertFalse(placementVideoDTO.isCompetitiveSeparation());
  }

  @Test
  @SneakyThrows
  void shouldFetchPlacementVideoDTOFromPositionNullChecks() {
    VideoLinearity LINEARITY = VideoLinearity.LINEAR;
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setVideoLinearity(null);

    PlacementVideoDTO placementVideoDTO =
        PlacementVideoDTOMapper.MAPPER.populatePlacementVideoDTOFromPosition(placementDTO);

    assertNotNull(placementVideoDTO);
    assertEquals(LINEARITY.toString(), placementVideoDTO.getLinearity().toString());

    assertFalse(placementVideoDTO.isLongform());
    assertNull(placementVideoDTO.getStreamType());
    assertNull(placementVideoDTO.getPlayerBrand());
    assertNull(placementVideoDTO.getSsai());
  }

  @Test
  @SneakyThrows
  void shouldFetchPlacementDTOVideoFieldsFromPlacementVideoDTO() {
    VideoLinearity LINEARITY = VideoLinearity.NON_LINEAR;
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setVideoLinearity(LINEARITY);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementDTO.setPlacementVideo(placementVideoDTO);

    PlacementVideoDTOMapper.MAPPER.populatePositionVideoFieldsFromPlacementVideoDTO(placementDTO);

    assertNotNull(placementVideoDTO);
    assertEquals(
        placementDTO.getVideoLinearity().toString(), placementVideoDTO.getLinearity().toString());
  }

  @Test
  void shouldSetLinearityToLinearWhenPlacementVideoDTOOrLinearityIsNull() {
    // Case 1: placementVideoDTO is null
    PublisherPositionDTO publisherPositionDTO = createPublisherPositionDTO();
    publisherPositionDTO.setPlacementVideo(null);

    PlacementVideoDTOMapper.MAPPER.populatePositionVideoFieldsFromPlacementVideoDTO(
        publisherPositionDTO);

    assertNotNull(publisherPositionDTO.getVideoLinearity());
    assertEquals(VideoLinearity.LINEAR, publisherPositionDTO.getVideoLinearity());

    // Case 2: placementVideoDTO is not null, but linearity is null
    PlacementVideoDTO placementVideoDTO = new PlacementVideoDTO();
    placementVideoDTO.setLinearity(null);
    publisherPositionDTO.setPlacementVideo(placementVideoDTO);

    PlacementVideoDTOMapper.MAPPER.populatePositionVideoFieldsFromPlacementVideoDTO(
        publisherPositionDTO);

    assertNotNull(placementVideoDTO.getLinearity());
    assertEquals(PlacementVideoLinearity.LINEAR, placementVideoDTO.getLinearity());
    assertEquals(VideoLinearity.LINEAR, publisherPositionDTO.getVideoLinearity());
  }

  @Test
  @SneakyThrows
  void shouldFetchPublisherPostionDTOVideoFieldsFromPlacementVideoDTO() {
    VideoLinearity LINEARITY = VideoLinearity.NON_LINEAR;
    PublisherPositionDTO publisherPositionDTO = createPublisherPositionDTO();
    publisherPositionDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPositionDTO.setVideoLinearity(LINEARITY);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    publisherPositionDTO.setPlacementVideo(placementVideoDTO);

    PlacementVideoDTOMapper.MAPPER.populatePositionVideoFieldsFromPlacementVideoDTO(
        publisherPositionDTO);

    assertNotNull(placementVideoDTO);
    assertEquals(
        publisherPositionDTO.getVideoLinearity().toString(),
        placementVideoDTO.getLinearity().toString());
  }

  @Test
  @SneakyThrows
  void shouldFetchPositionVideoFieldsFromPlacementVideoDTONullChecks() {
    VideoLinearity LINEARITY = VideoLinearity.NON_LINEAR;
    PlacementDTO placementDTO = createPlacementDTO(123L);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setVideoLinearity(LINEARITY);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLinearity(null);
    placementDTO.setPlacementVideo(placementVideoDTO);

    PlacementVideoDTOMapper.MAPPER.populatePositionVideoFieldsFromPlacementVideoDTO(placementDTO);

    assertNotNull(placementVideoDTO);
    assertEquals(
        placementDTO.getVideoLinearity().toString(), placementVideoDTO.getLinearity().toString());
  }

  @Test
  @SneakyThrows
  void shouldFetchDapO2PlacementVideoAndSetToNullForDefaultValues() {
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setDapPlayerType(DapPlayerType.O2);
    placementVideo.setPlayerId(DapVideoPlacementUtil.DEFAULT_O2_PLAYER_ID);
    placementVideo.setPlayListId(DapVideoPlacementUtil.DEFAULT_O2_PLAYLIST_ID);

    PlacementVideoDTO placementVideoDTO = PlacementVideoDTOMapper.MAPPER.map(placementVideo);

    assertNotNull(placementVideoDTO);
    assertEquals(DapPlayerType.O2, placementVideoDTO.getDapPlayerType());
    assertNull(placementVideoDTO.getPlayerId());
    assertNull(placementVideoDTO.getPlayListId());
  }

  @Test
  @SneakyThrows
  void shouldFetchDapYahooPlacementVideoAndSetToNullForDefaultValues() {
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setDapPlayerType(DapPlayerType.YAHOO);
    placementVideo.setPlayListId(DapVideoPlacementUtil.DEFAULT_YAHOO_PLAYLIST_ID);

    PlacementVideoDTO placementVideoDTO = PlacementVideoDTOMapper.MAPPER.map(placementVideo);

    assertNotNull(placementVideoDTO);
    assertEquals(DapPlayerType.YAHOO, placementVideoDTO.getDapPlayerType());
    assertNull(placementVideoDTO.getPlayerId());
    assertNull(placementVideoDTO.getPlayListId());
  }

  @Test
  @SneakyThrows
  void shouldFetchDapO2PlacementVideoWithNonDefaultValues() {
    String test_player_id = "TEST-PLAYER-ID";
    String test_playlist_id = "TEST-PLAYLSIT-ID";
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setDapPlayerType(DapPlayerType.O2);
    placementVideo.setPlayListId(test_playlist_id);
    placementVideo.setPlayerId(test_player_id);

    PlacementVideoDTO placementVideoDTO = PlacementVideoDTOMapper.MAPPER.map(placementVideo);

    assertNotNull(placementVideoDTO);
    assertEquals(DapPlayerType.O2, placementVideoDTO.getDapPlayerType());
    assertEquals(test_player_id, placementVideoDTO.getPlayerId());
    assertEquals(test_playlist_id, placementVideoDTO.getPlayListId());
  }

  @Test
  @SneakyThrows
  void shouldFetchDapYahooPlacementVideoWithNonDefaultValues() {
    String test_playlist_id = "TEST-PLAYLSIT-ID";
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setDapPlayerType(DapPlayerType.YAHOO);
    placementVideo.setPlayListId(test_playlist_id);

    PlacementVideoDTO placementVideoDTO = PlacementVideoDTOMapper.MAPPER.map(placementVideo);

    assertNotNull(placementVideoDTO);
    assertEquals(DapPlayerType.YAHOO, placementVideoDTO.getDapPlayerType());
    assertNull(placementVideoDTO.getPlayerId());
    assertEquals(test_playlist_id, placementVideoDTO.getPlayListId());
  }

  @Test
  @SneakyThrows
  void shouldMapDapO2PlacementVideoAndSetDefaultValuesIncaseofNull() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setDapPlayerType(DapPlayerType.O2);

    PlacementVideo placementVideo = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);

    assertNotNull(placementVideo);
    assertEquals(DapPlayerType.O2, placementVideo.getDapPlayerType());
    assertEquals(DapVideoPlacementUtil.DEFAULT_O2_PLAYER_ID, placementVideo.getPlayerId());
    assertEquals(DapVideoPlacementUtil.DEFAULT_O2_PLAYLIST_ID, placementVideo.getPlayListId());
  }

  @Test
  @SneakyThrows
  void shouldMapDapYahooPlacementVideoAndSetDefaultValuesIncaseofNull() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setDapPlayerType(DapPlayerType.YAHOO);

    PlacementVideo placementVideo = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);

    assertNotNull(placementVideo);
    assertEquals(DapPlayerType.YAHOO, placementVideo.getDapPlayerType());
    assertNull(placementVideo.getPlayerId());
    assertEquals(DapVideoPlacementUtil.DEFAULT_YAHOO_PLAYLIST_ID, placementVideo.getPlayListId());
  }

  @Test
  @SneakyThrows
  void shouldNotMapAnyDapParamsWhenDapPlayerTypeIsNull() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setDapPlayerType(null);

    PlacementVideo placementVideo = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);

    assertNotNull(placementVideo);
    assertNull(placementVideo.getDapPlayerType());
    assertNull(placementVideo.getPlayerId());
    assertNull(placementVideo.getPlayListId());
  }

  @Test
  @SneakyThrows
  void shouldNotMapAnyDapParamsWhenDapPlayerParamsArePassedForO2() {
    String VALID_TEST_PLAYER_ID_PLAYLIST_ID = "Valid-PLayer-id-and-playlist-id-123";
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setDapPlayerType(DapPlayerType.O2);
    placementVideoDTO.setPlayerId(VALID_TEST_PLAYER_ID_PLAYLIST_ID);
    placementVideoDTO.setPlayListId(VALID_TEST_PLAYER_ID_PLAYLIST_ID);

    PlacementVideo placementVideo = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);

    assertNotNull(placementVideo);
    assertEquals(DapPlayerType.O2, placementVideo.getDapPlayerType());
    assertEquals(VALID_TEST_PLAYER_ID_PLAYLIST_ID, placementVideo.getPlayerId());
    assertEquals(VALID_TEST_PLAYER_ID_PLAYLIST_ID, placementVideo.getPlayListId());
  }

  @Test
  @SneakyThrows
  void shouldNotMapAnyDapParamsWhenDapPlayerParamsArePassedForYahoo() {
    String VALID_TEST_PLAYER_ID_PLAYLIST_ID = "Valid-PLayer-id-and-playlist-id-123";
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setDapPlayerType(DapPlayerType.YAHOO);
    placementVideoDTO.setPlayListId(VALID_TEST_PLAYER_ID_PLAYLIST_ID);

    PlacementVideo placementVideo = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);

    assertNotNull(placementVideo);
    assertEquals(DapPlayerType.YAHOO, placementVideo.getDapPlayerType());
    assertEquals(VALID_TEST_PLAYER_ID_PLAYLIST_ID, placementVideo.getPlayListId());
  }

  private PublisherPositionDTO createPublisherPositionDTO() {
    PublisherPositionDTO publisherPositionDTO = PublisherPositionDTO.builder().build();
    publisherPositionDTO.setWidth(300);
    publisherPositionDTO.setHeight(50);
    publisherPositionDTO.setName("foo");
    publisherPositionDTO.setMemo("foo");
    publisherPositionDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPositionDTO.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    publisherPositionDTO.setPlacementCategory(PlacementCategory.BANNER);
    publisherPositionDTO.setInterstitial(false);
    publisherPositionDTO.setMraidAdvancedTracking(FALSE);
    publisherPositionDTO.setMraidSupport(MRAIDSupport.YES);
    publisherPositionDTO.setAdSizeType(AdSizeType.STANDARD);

    return publisherPositionDTO;
  }

  private PlacementDTO createPlacementDTO(Long sitePid) {
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(sitePid);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setPid(DEFAULT_PID);
    placementDTO.setWidth(100);
    placementDTO.setHeight(100);
    placementDTO.setName("foo");
    placementDTO.setMemo("foo");
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setSite(siteDTO);
    return placementDTO;
  }
}
