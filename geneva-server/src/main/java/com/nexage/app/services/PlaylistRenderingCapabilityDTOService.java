package com.nexage.app.services;

import com.nexage.app.dto.PlaylistRenderingCapabilityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines business logic operations for interacting with {@link PlaylistRenderingCapabilityDTO}.
 */
public interface PlaylistRenderingCapabilityDTOService {

  /**
   * Get page of {@link PlaylistRenderingCapabilityDTO}.
   *
   * @param pageable the specification of the page to get
   * @return page of {@link PlaylistRenderingCapabilityDTO}
   */
  Page<PlaylistRenderingCapabilityDTO> getPage(Pageable pageable);
}
