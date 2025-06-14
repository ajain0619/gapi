package com.nexage.app.services;

import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.app.dto.seller.PlacementCommonDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.PlacementVideoPlaylistDTO;
import java.util.List;

public interface PlacementVideoService {

  /**
   * Save a {@link PlacementVideoDTO}
   *
   * @param placementVideoDTO {@link PlacementVideoDTO}
   * @param positionPid {@link Long}
   * @return {@link PlacementVideoDTO}
   */
  PlacementVideoDTO save(PlacementVideoDTO placementVideoDTO, Long positionPid);

  /**
   * Updates a {@link PlacementVideoDTO}
   *
   * @param placementVideoDTO {@link PlacementVideoDTO}
   * @param positionPid {@link Long}
   * @return {@link PlacementVideoDTO}
   */
  PlacementVideoDTO update(PlacementVideoDTO placementVideoDTO, Long positionPid);

  /**
   * Update/Save {@link PlacementVideoDTO} based on if it exists in database This method is
   * responsible to find if we need to update the placementVideo or save it in database. Eg: When a
   * placement converts from BANNER/NATIVE to VIDEO/VIDEO_AND_BANNER, in this scenario we will get a
   * request to save placementVideo instead of update in update request for placement and for other
   * scenarios we should be updating the placementVideo
   *
   * @param placementVideoDTO {@link PlacementVideoDTO}
   * @param positionPid {@link Long}
   * @param insertPlacementVideo {@link boolean}
   * @return {@link PlacementVideoDTO}
   */
  PlacementVideoDTO update(
      PlacementVideoDTO placementVideoDTO, Long positionPid, boolean insertPlacementVideo);

  /**
   * populates Video data to/from {@link PlacementVideoDTO}
   *
   * @param publisherPosition {@link PlacementCommonDTO}
   * @param placementVideoDTO {@link PlacementVideoDTO}
   * @return {@link PlacementVideoDTO}
   */
  PlacementVideoDTO populateVideoData(
      PlacementVideoDTO placementVideoDTO, PlacementCommonDTO publisherPosition);

  /**
   * Deletes a {@link PlacementVideoDTO} and all its playlist info
   *
   * @param placementVideoDTO {@link PlacementVideoDTO}
   * @return {@link boolean}
   */
  boolean delete(PlacementVideoDTO placementVideoDTO);

  /**
   * Deletes a {@link PlacementVideoDTO}
   *
   * @param placementVideoPid {@link Long}
   * @return {@link boolean}
   */
  boolean delete(Long placementVideoPid);

  /**
   * Get a {@link PlacementVideoDTO}
   *
   * @param pid {@link Long}
   * @return {@link PlacementVideoDTO}
   */
  PlacementVideoDTO getPlacementVideo(Long pid);

  /**
   * Get a list of {@link PlacementVideoPlaylistDTO} for a placement video
   *
   * @param placementVideo {@link PlacementVideo}
   * @return {@link List}
   */
  List<PlacementVideoPlaylistDTO> getPlaylistInfo(PlacementVideo placementVideo);
}
