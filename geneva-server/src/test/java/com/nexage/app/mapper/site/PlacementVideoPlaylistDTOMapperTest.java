package com.nexage.app.mapper.site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoPlaylist;
import com.nexage.app.dto.seller.PlacementVideoPlaylistDTO;
import com.nexage.app.web.support.TestObjectsFactory;
import org.junit.jupiter.api.Test;

class PlacementVideoPlaylistDTOMapperTest {

  @Test
  void shouldMapPlaylistInfoToPlaylistInfoDTO() {
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    PlacementVideoPlaylist placementVideoPlaylist =
        TestObjectsFactory.createDefaultPlacementVideoPlaylist(placementVideo);

    PlacementVideoPlaylistDTO placementVideoPlaylistDTO =
        PlacementVideoPlaylistDTOMapper.MAPPER.map(placementVideoPlaylist);
    assertNotNull(placementVideoPlaylistDTO);
    assertEquals(placementVideoPlaylistDTO.getPid(), placementVideoPlaylist.getPid());
    assertEquals(
        placementVideoPlaylistDTO.getPlacementVideoPid(),
        placementVideoPlaylist.getPlacementVideoPid().getPid());
    assertEquals(
        placementVideoPlaylistDTO.getFallbackURL(), placementVideoPlaylist.getFallbackURL());
    assertEquals(placementVideoPlaylistDTO.getMediaType(), placementVideoPlaylist.getMediaType());
  }

  @Test
  void shouldMapPlaylistInfoDTOToPlaylistInfo() {
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    PlacementVideoPlaylistDTO placementVideoPlaylistDTO =
        TestObjectsFactory.createDefaultPlacementVideoPlaylistDTO(placementVideo.getPid());

    PlacementVideoPlaylist placementVideoPlaylist =
        PlacementVideoPlaylistDTOMapper.MAPPER.map(placementVideoPlaylistDTO, placementVideo);
    assertNotNull(placementVideoPlaylist);
    assertEquals(placementVideoPlaylist.getPid(), placementVideoPlaylistDTO.getPid());
    assertEquals(
        placementVideoPlaylist.getPlacementVideoPid().getPid(),
        placementVideoPlaylistDTO.getPlacementVideoPid());
    assertEquals(
        placementVideoPlaylist.getFallbackURL(), placementVideoPlaylistDTO.getFallbackURL());
    assertEquals(placementVideoPlaylist.getMediaType(), placementVideoPlaylistDTO.getMediaType());
  }
}
