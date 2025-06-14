package com.nexage.app.services.impl;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.mapper.HbPartnerAssignmentDTOMapper;
import com.nexage.app.services.NativePlacementHbPartnerService;
import com.nexage.app.util.assemblers.PublisherPositionAssembler;
import com.nexage.app.util.assemblers.context.PublisherPositionContext;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Log4j2
@PreAuthorize(
    "@loginUserContext.isNexageAdminOrManager() or @loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
public class NativePlacementHbPartnerServiceImpl implements NativePlacementHbPartnerService {

  private final PublisherPositionAssembler publisherPositionAssembler;

  /** {@inheritDoc} */
  @Override
  public void handleHbPartnersAssignmentMapping(
      NativePlacementDTO nativePlacement, Position position, Site site) {
    PublisherPositionContext context = PublisherPositionContext.newBuilder().withSite(site).build();
    publisherPositionAssembler.fillHbPartnerAttributes(
        context,
        position,
        nativePlacement.getHbPartnerAttributes(),
        nativePlacement.getPlacementVideo());
  }

  /** {@inheritDoc} */
  @Override
  public void handleHbPartnerPositionMapping(
      NativePlacementDTO nativePlacement, Position position) {
    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOS =
        HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerPosition(position.getHbPartnerPosition());
    nativePlacement.setHbPartnerAttributes(hbPartnerAssignmentDTOS);
  }
}
