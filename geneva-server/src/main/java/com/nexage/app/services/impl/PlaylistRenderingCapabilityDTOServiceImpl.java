package com.nexage.app.services.impl;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.repository.PlaylistRenderingCapabilityRepository;
import com.nexage.app.dto.PlaylistRenderingCapabilityDTO;
import com.nexage.app.mapper.PlaylistRenderingCapabilityDTOMapper;
import com.nexage.app.services.PlaylistRenderingCapabilityDTOService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/** {@inheritDoc} */
@Service
public class PlaylistRenderingCapabilityDTOServiceImpl
    implements PlaylistRenderingCapabilityDTOService {

  private final PlaylistRenderingCapabilityRepository playlistRenderingCapabilityRepository;
  private final PlaylistRenderingCapabilityDTOMapper playlistRenderingCapabilityDTOMapper;

  PlaylistRenderingCapabilityDTOServiceImpl(
      PlaylistRenderingCapabilityRepository playlistRenderingCapabilityRepository,
      PlaylistRenderingCapabilityDTOMapper playlistRenderingCapabilityDTOMapper) {
    this.playlistRenderingCapabilityRepository = playlistRenderingCapabilityRepository;
    this.playlistRenderingCapabilityDTOMapper = playlistRenderingCapabilityDTOMapper;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage()"
          + " OR @loginUserContext.isOcUserSeller()"
          + " OR @loginUserContext.isOcUserSellerSeat()")
  public Page<PlaylistRenderingCapabilityDTO> getPage(Pageable pageable) {
    return playlistRenderingCapabilityRepository
        .findAllByStatus(pageable, Status.ACTIVE)
        .map(playlistRenderingCapabilityDTOMapper::map);
  }
}
