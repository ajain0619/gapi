package com.nexage.app.mapper;

import com.nexage.admin.core.model.PlaylistRenderingCapability;
import com.nexage.app.dto.PlaylistRenderingCapabilityDTO;
import org.mapstruct.Mapper;

/**
 * Provides mappings from {@link PlaylistRenderingCapability} to {@link
 * PlaylistRenderingCapabilityDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlaylistRenderingCapabilityDTOMapper {

  /**
   * Map from {@link PlaylistRenderingCapability} to {@link PlaylistRenderingCapabilityDTO}
   *
   * @param playlistRenderingCapability the {@link PlaylistRenderingCapability} to map
   * @return the mapped {@link PlaylistRenderingCapabilityDTO}
   */
  PlaylistRenderingCapabilityDTO map(PlaylistRenderingCapability playlistRenderingCapability);
}
