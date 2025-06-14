package com.nexage.app.mapper.site;

import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.PlacementVideoLinearity;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoCompanion;
import com.nexage.app.dto.seller.PlacementCommonDTO;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.PlacementVideoPlaylistDTO;
import com.nexage.app.util.placement.DapVideoPlacementUtil;
import java.util.List;
import java.util.Objects;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = PlacementVideoCompanionDTOMapper.class)
public interface PlacementVideoDTOMapper {

  PlacementVideoDTOMapper MAPPER = Mappers.getMapper(PlacementVideoDTOMapper.class);

  PlacementVideoDTO map(PlacementVideo source);

  PlacementVideoDTO map(
      PlacementVideo source, List<PlacementVideoPlaylistDTO> placementVideoPlaylistDTO);

  @InheritInverseConfiguration
  PlacementVideo map(PlacementVideoDTO placementVideoDTO);

  @AfterMapping
  default void after(@MappingTarget PlacementVideo placementVideo) {
    PlacementVideoLinearity linearity = placementVideo.getLinearity();
    if (Objects.isNull(linearity)) placementVideo.setLinearity(PlacementVideoLinearity.LINEAR);

    if (Objects.nonNull(placementVideo.getCompanions())) {
      for (PlacementVideoCompanion companion : placementVideo.getCompanions()) {
        companion.setPlacementVideo(placementVideo);
      }
    }

    DapPlayerType dapPlayerType = placementVideo.getDapPlayerType();

    if (Objects.isNull(dapPlayerType)) {
      return;
    }

    if (DapPlayerType.O2 == dapPlayerType) {
      if (Objects.isNull(placementVideo.getPlayerId())) {
        placementVideo.setPlayerId(DapVideoPlacementUtil.DEFAULT_O2_PLAYER_ID);
      }
      if (Objects.isNull(placementVideo.getPlayListId())) {
        placementVideo.setPlayListId(DapVideoPlacementUtil.DEFAULT_O2_PLAYLIST_ID);
      }
    } else {
      if (Objects.isNull(placementVideo.getPlayListId())) {
        placementVideo.setPlayListId(DapVideoPlacementUtil.DEFAULT_YAHOO_PLAYLIST_ID);
      }
    }
  }

  @AfterMapping
  default void after(
      @MappingTarget PlacementVideoDTO placementVideoDTO,
      List<PlacementVideoPlaylistDTO> placementVideoPlaylistDTO) {
    if (!placementVideoPlaylistDTO.isEmpty()) {
      placementVideoDTO.setPlaylistInfo(placementVideoPlaylistDTO);
    }
  }

  @AfterMapping
  default void after(@MappingTarget PlacementVideoDTO placementVideoDTO) {
    if (Objects.nonNull(placementVideoDTO.getPlayerId())
        && placementVideoDTO.getPlayerId().equals(DapVideoPlacementUtil.DEFAULT_O2_PLAYER_ID)) {
      placementVideoDTO.setPlayerId(null);
    }
    if (Objects.nonNull(placementVideoDTO.getPlayListId())
        && (DapVideoPlacementUtil.DEFAULT_O2_PLAYLIST_ID.equals(placementVideoDTO.getPlayListId())
            || DapVideoPlacementUtil.DEFAULT_YAHOO_PLAYLIST_ID.equals(
                placementVideoDTO.getPlayListId()))) {
      placementVideoDTO.setPlayListId(null);
    }
  }

  /**
   * populate data from Position {@link PlacementVideoDTO}
   *
   * @param placementDTO {@link PlacementDTO}
   * @return {@link PlacementVideoDTO}
   */
  default PlacementVideoDTO populatePlacementVideoDTOFromPosition(PlacementCommonDTO placementDTO) {
    PlacementVideoDTO placementVideoDTO = new PlacementVideoDTO();

    VideoLinearity linearity = placementDTO.getVideoLinearity();
    if (Objects.nonNull(linearity))
      placementVideoDTO.setLinearity(PlacementVideoLinearity.valueOf(linearity.toString()));
    else placementVideoDTO.setLinearity(PlacementVideoLinearity.LINEAR);

    // Default values
    placementVideoDTO.setPlayerRequired(false);
    placementVideoDTO.setLongform(false);
    return placementVideoDTO;
  }

  /**
   * populate data to Position {@link PlacementVideoDTO}
   *
   * @param placementDTO {@link PlacementDTO}
   * @return {@link null}
   */
  default void populatePositionVideoFieldsFromPlacementVideoDTO(PlacementCommonDTO placementDTO) {
    PlacementVideoDTO placementVideoDTO = placementDTO.getPlacementVideo();
    PlacementVideoLinearity linearity = null;

    if (placementVideoDTO != null) {
      linearity = placementVideoDTO.getLinearity();
      if (Objects.isNull(linearity)) {
        linearity = PlacementVideoLinearity.LINEAR;
        placementVideoDTO.setLinearity(linearity);
      }
    } else {
      linearity = PlacementVideoLinearity.LINEAR;
    }

    placementDTO.setVideoLinearity(VideoLinearity.valueOf(linearity.toString()));
  }
}
