package com.nexage.app.services.impl;

import com.nexage.admin.core.repository.IdentityProviderRepository;
import com.nexage.app.dto.IdentityProviderDTO;
import com.nexage.app.mapper.IdentityProviderDTOMapper;
import com.nexage.app.services.IdentityProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class IdentityProviderServiceImpl implements IdentityProviderService {

  private final IdentityProviderRepository identityProviderRepository;

  @Autowired
  public IdentityProviderServiceImpl(IdentityProviderRepository identityProviderRepository) {
    this.identityProviderRepository = identityProviderRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<IdentityProviderDTO> getAllIdentityProviders(Pageable pageable) {
    return identityProviderRepository.findAll(pageable).map(IdentityProviderDTOMapper.MAPPER::map);
  }
}
