package com.nexage.app.services.impl;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.services.NativePlacementTierService;
import com.nexage.app.util.PositionTrafficTypeValidator;
import com.nexage.app.util.assemblers.PositionTierAssembler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Log4j2
@PreAuthorize(
    "@loginUserContext.isNexageAdminOrManager() or @loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
public class NativePlacementTierServiceImpl implements NativePlacementTierService {

  private final PositionTierAssembler positionTierAssembler;
  private final PositionTrafficTypeValidator positionTrafficTypeValidator;

  @Autowired
  public NativePlacementTierServiceImpl(
      PositionTierAssembler positionTierAssembler,
      PositionTrafficTypeValidator positionTrafficTypeValidator) {
    this.positionTierAssembler = positionTierAssembler;
    this.positionTrafficTypeValidator = positionTrafficTypeValidator;
  }

  @Override
  public void update(NativePlacementDTO nativePlacementDTO, Site site, Position position) {
    positionTrafficTypeValidator.validatePositionTiers(
        nativePlacementDTO.getTiers(), site, position);
    positionTierAssembler.handleTiers(position, nativePlacementDTO.getTiers());
  }
}
