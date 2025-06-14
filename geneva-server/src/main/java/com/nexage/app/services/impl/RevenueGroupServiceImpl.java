package com.nexage.app.services.impl;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.repository.RevenueGroupRepository;
import com.nexage.app.dto.RevenueGroupDTO;
import com.nexage.app.mapper.RevenueGroupDTOMapper;
import com.nexage.app.services.RevenueGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RevenueGroupServiceImpl implements RevenueGroupService {

  private final RevenueGroupRepository revenueGroupRepository;

  /** {@inheritDoc} */
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  @Override
  public Page<RevenueGroupDTO> getRevenueGroups(Pageable pageable) {
    return revenueGroupRepository
        .findAllByStatus(Status.ACTIVE, pageable)
        .map(RevenueGroupDTOMapper.MAPPER::map);
  }
}
