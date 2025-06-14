package com.nexage.app.services.impl;

import com.nexage.admin.core.repository.RuleDSPRepository;
import com.nexage.app.dto.RuleDSPBiddersDTO;
import com.nexage.app.mapper.RuleDSPBiddersDTOMapper;
import com.nexage.app.services.RuleDSPService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Log4j2
@Service
@Transactional
public class RuleDSPServiceImpl implements RuleDSPService {

  private final RuleDSPRepository ruleDSPRepository;

  /** {@inheritDoc} */
  @Override
  public List<RuleDSPBiddersDTO> findAll() {

    return ruleDSPRepository.findDSPsWithActiveBidders().stream()
        .map(RuleDSPBiddersDTOMapper.MAPPER::map)
        .collect(Collectors.toList());
  }
}
