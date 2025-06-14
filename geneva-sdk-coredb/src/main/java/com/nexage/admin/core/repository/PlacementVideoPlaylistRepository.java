package com.nexage.admin.core.repository;

import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoPlaylist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlacementVideoPlaylistRepository
    extends JpaRepository<PlacementVideoPlaylist, Long> {

  /**
   * Find all playlist info in a list by placement video pid
   *
   * @param placementVideoPid @{link Long}
   * @return {@link List}
   */
  List<PlacementVideoPlaylist> findAllByPlacementVideoPid(PlacementVideo placementVideoPid);
}
