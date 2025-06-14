package com.nexage.app.mapper.site;

import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoPlaylist;
import com.nexage.app.dto.seller.PlacementVideoPlaylistDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlacementVideoPlaylistDTOMapper {
  PlacementVideoPlaylistDTOMapper MAPPER = Mappers.getMapper(PlacementVideoPlaylistDTOMapper.class);

  @Mapping(source = "placementVideoPid.pid", target = "placementVideoPid")
  PlacementVideoPlaylistDTO map(PlacementVideoPlaylist source);

  default PlacementVideoPlaylist map(
      PlacementVideoPlaylistDTO placementVideoPlaylistDTO, PlacementVideo placementVideo) {
    PlacementVideoPlaylist placementVideoPlaylist = new PlacementVideoPlaylist();
    placementVideoPlaylist.setPid(placementVideoPlaylistDTO.getPid());
    placementVideoPlaylist.setPlacementVideoPid(placementVideo);
    placementVideoPlaylist.setFallbackURL(placementVideoPlaylistDTO.getFallbackURL());
    placementVideoPlaylist.setMediaType(placementVideoPlaylistDTO.getMediaType());
    placementVideoPlaylist.setVersion(placementVideoPlaylistDTO.getVersion());

    return placementVideoPlaylist;
  }
}
