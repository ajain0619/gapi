package com.nexage.app.services.impl;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.dto.seller.nativeads.NativePlacementDTO;
import com.nexage.app.util.PositionTrafficTypeValidator;
import com.nexage.app.util.assemblers.PositionTierAssembler;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NativePlacementTierServiceImplTest {

  @Mock private PositionTierAssembler positionTierAssembler;
  @Mock private PositionTrafficTypeValidator positionTrafficTypeValidator;
  @InjectMocks private NativePlacementTierServiceImpl nativePlacementTierService;

  @Test
  void update() {
    PublisherTierDTO tier = new PublisherTierDTO();
    Set<PublisherTierDTO> tiers = Set.of(tier);
    NativePlacementDTO nativePlacementDTO = new NativePlacementDTO();
    nativePlacementDTO.setTiers(tiers);
    Site site = new Site();
    Position position = new Position();

    nativePlacementTierService.update(nativePlacementDTO, site, position);

    verify(positionTrafficTypeValidator)
        .validatePositionTiers(same(tiers), same(site), same(position));
    verify(positionTierAssembler).handleTiers(same(position), same(tiers));
  }
}
