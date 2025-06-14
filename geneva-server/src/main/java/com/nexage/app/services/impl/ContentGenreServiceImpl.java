package com.nexage.app.services.impl;

import com.nexage.admin.core.repository.ContentGenreRepository;
import com.nexage.admin.core.specification.GeneralSpecification;
import com.nexage.app.dto.ContentGenreDTO;
import com.nexage.app.mapper.ContentGenreDTOMapper;
import com.nexage.app.services.ContentGenreService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ContentGenreServiceImpl implements ContentGenreService {
  private final ContentGenreRepository contentGenreRepository;

  @Autowired
  public ContentGenreServiceImpl(ContentGenreRepository contentGenreRepository) {
    this.contentGenreRepository = contentGenreRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ContentGenreDTO> findAll(String qt, Set<String> qf, Pageable pageable) {
    return contentGenreRepository
        .findAll(GeneralSpecification.withSearchCriteria(qf, qt), pageable)
        .map(ContentGenreDTOMapper.MAPPER::map);
  }
}
