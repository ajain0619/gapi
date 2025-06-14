package com.nexage.app.services.impl;

import com.nexage.admin.core.repository.IsoLanguageRepository;
import com.nexage.admin.core.specification.GeneralSpecification;
import com.nexage.app.dto.IsoLanguageDTO;
import com.nexage.app.mapper.IsoLanguageDTOMapper;
import com.nexage.app.services.IsoLanguageService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class IsoLanguageServiceImpl implements IsoLanguageService {

  private final IsoLanguageRepository isoLanguageRepository;

  @Autowired
  public IsoLanguageServiceImpl(IsoLanguageRepository isoLanguageRepository) {
    this.isoLanguageRepository = isoLanguageRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<IsoLanguageDTO> findAll(String qt, Set<String> qf, Pageable pageable) {
    return isoLanguageRepository
        .findAll(GeneralSpecification.withSearchCriteria(qf, qt), pageable)
        .map(IsoLanguageDTOMapper.MAPPER::map);
  }
}
