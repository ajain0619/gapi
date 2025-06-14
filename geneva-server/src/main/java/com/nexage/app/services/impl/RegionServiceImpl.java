package com.nexage.app.services.impl;

import com.nexage.admin.core.repository.RegionRepository;
import com.nexage.admin.core.sparta.jpa.model.Region;
import com.nexage.app.services.RegionService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@PreAuthorize("@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()")
public class RegionServiceImpl implements RegionService {

  private final RegionRepository regionRepository;

  @Override
  public List<Region> getAllRegions() {
    return regionRepository.findAll();
  }
}
