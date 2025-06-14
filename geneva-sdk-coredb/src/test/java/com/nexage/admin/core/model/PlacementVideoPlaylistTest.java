package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.enums.MediaType;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoPlaylist;
import org.junit.jupiter.api.Test;

class PlacementVideoPlaylistTest {
  @Test
  void shouldPrePersist() {
    PlacementVideo placementVideo = new PlacementVideo();
    placementVideo.setPid(1L);

    PlacementVideoPlaylist placementVideoPlaylist = new PlacementVideoPlaylist();
    placementVideoPlaylist.setPlacementVideoPid(placementVideo);
    placementVideoPlaylist.setPid(1L);
    placementVideoPlaylist.setFallbackURL("someurl.mp4");
    placementVideoPlaylist.setMediaType(MediaType.VIDEO_MP4);
    placementVideoPlaylist.prePersist();

    assertNotNull(placementVideoPlaylist.getCreatedOn());
    assertNotNull(placementVideoPlaylist.getUpdatedOn());
  }

  @Test
  void shouldPreUpdate() {
    PlacementVideo placementVideo = new PlacementVideo();
    placementVideo.setPid(1L);

    PlacementVideoPlaylist placementVideoPlaylist = new PlacementVideoPlaylist();
    placementVideoPlaylist.setPlacementVideoPid(placementVideo);
    placementVideoPlaylist.setPid(1L);
    placementVideoPlaylist.setFallbackURL("someurl.mp4");
    placementVideoPlaylist.setMediaType(MediaType.VIDEO_MP4);
    placementVideoPlaylist.preUpdate();

    assertNotNull(placementVideoPlaylist.getUpdatedOn());
  }
}
