package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.model.HbPartnerPosition;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.util.assemblers.PublisherPositionAssembler;
import com.nexage.app.util.assemblers.context.PublisherPositionContext;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NativePlacementHbPartnerServiceImplTest {

  @InjectMocks NativePlacementHbPartnerServiceImpl nativePlacementHbPartnerService;

  @Mock private PublisherPositionAssembler publisherPositionAssembler;

  @Captor ArgumentCaptor<PublisherPositionContext> contextCaptor;

  @Test
  void testHandleHbPartners() {
    NativePlacementDTO nativePlacement = new NativePlacementDTO();
    nativePlacement.setSitePid(123L);
    Set<HbPartnerAssignmentDTO> hbAttrs = new HashSet<>();
    hbAttrs.add(new HbPartnerAssignmentDTO());
    nativePlacement.setHbPartnerAttributes(hbAttrs);
    Position position = new Position();
    position.setAdSize("24");
    Site site = new Site();
    site.setCompanyPid(555L);

    nativePlacementHbPartnerService.handleHbPartnersAssignmentMapping(
        nativePlacement, position, site);

    verify(publisherPositionAssembler, times(1))
        .fillHbPartnerAttributes(
            contextCaptor.capture(),
            eq(position),
            eq(nativePlacement.getHbPartnerAttributes()),
            eq(nativePlacement.getPlacementVideo()));

    PublisherPositionContext expectedContext = contextCaptor.getValue();
    assertEquals(site, expectedContext.getSite());
  }

  @Test
  void testHandleHbPartnerPositionMapping() {

    NativePlacementDTO nativePlacementDTO = new NativePlacementDTO();
    Position position = new Position();
    Set<HbPartnerPosition> hbPartPos = Set.of(new HbPartnerPosition());
    position.setHbPartnerPosition(hbPartPos);
    nativePlacementHbPartnerService.handleHbPartnerPositionMapping(nativePlacementDTO, position);

    assertEquals(
        nativePlacementDTO.getHbPartnerAttributes().size(), position.getHbPartnerPosition().size());
  }
}
