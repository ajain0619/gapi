package com.nexage.app.services.impl;

import com.nexage.admin.core.repository.ContentRatingRepository;
import com.nexage.admin.core.specification.GeneralSpecification;
import com.nexage.app.dto.ContentRatingDTO;
import com.nexage.app.mapper.ContentRatingDTOMapper;
import com.nexage.app.services.ContentRatingService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ContentRatingServiceImpl implements ContentRatingService {
  private final ContentRatingRepository contentRatingRepository;

  @Autowired
  public ContentRatingServiceImpl(ContentRatingRepository contentRatingRepository) {
    this.contentRatingRepository = contentRatingRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ContentRatingDTO> findAll(String qt, Set<String> qf, Pageable pageable) {
    return contentRatingRepository
        .findAll(GeneralSpecification.withSearchCriteria(qf, qt), pageable)
        .map(ContentRatingDTOMapper.MAPPER::map);
  }
}
