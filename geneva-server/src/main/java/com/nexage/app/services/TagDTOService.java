package com.nexage.app.services;

import com.nexage.app.dto.tag.TagDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagDTOService {

  /**
   * Find all {@link TagDTO} under request criteria, returning a paginated response.
   *
   * @param sellerId sellerId
   * @param siteId siteId
   * @param placementId placementId
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link TagDTO} instances based on parameters.
   */
  Page<TagDTO> getTags(Long sellerId, Long siteId, Long placementId, Pageable pageable);
}
