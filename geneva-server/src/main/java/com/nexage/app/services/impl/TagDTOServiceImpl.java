package com.nexage.app.services.impl;

import com.nexage.admin.core.repository.TagRepository;
import com.nexage.app.dto.tag.TagDTO;
import com.nexage.app.mapper.TagDTOMapper;
import com.nexage.app.services.TagDTOService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
public class TagDTOServiceImpl implements TagDTOService {

  private final TagRepository tagRepository;

  @Autowired
  public TagDTOServiceImpl(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  /** {@inheritDoc} */
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or "
          + "@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or "
          + "@loginUserContext.isOcUserSeller() or @loginUserContext.isOcUserBuyer()")
  public Page<TagDTO> getTags(Long sellerId, Long siteId, Long placementId, Pageable pageable) {
    return tagRepository.findTags(siteId, placementId, pageable).map(TagDTOMapper.MAPPER::map);
  }
}
