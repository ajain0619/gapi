package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.DealBidderConfigView;
import com.nexage.admin.core.model.RuleDSPBiddersView;
import com.nexage.admin.core.repository.RuleDSPRepository;
import com.nexage.app.dto.RuleDSPBiddersDTO;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleDSPBiddersServiceImplTest {

  @Mock private RuleDSPRepository ruleDSPRepository;

  @InjectMocks private RuleDSPServiceImpl subject;

  @Test
  void returnDSPsWithBidders() {

    Long companyPid = new Random().nextLong();

    DealBidderConfigView bidder1 = new DealBidderConfigView();
    bidder1.setCompanyPid(companyPid);
    bidder1.setName(UUID.randomUUID().toString());
    bidder1.setPid(new Random().nextLong());
    bidder1.setTrafficStatus(true);

    RuleDSPBiddersView buyersWithBidders = new RuleDSPBiddersView();

    Set<DealBidderConfigView> bidders = new HashSet<>();
    bidders.add(bidder1);

    buyersWithBidders.setName(UUID.randomUUID().toString());
    buyersWithBidders.setPid(companyPid);
    buyersWithBidders.setBidders(bidders);

    List<RuleDSPBiddersView> dspsWithBiddersDTOs = Collections.singletonList(buyersWithBidders);

    when(ruleDSPRepository.findDSPsWithActiveBidders()).thenReturn(dspsWithBiddersDTOs);

    List<RuleDSPBiddersDTO> result = subject.findAll();
    assertNotNull(result);
    assertEquals(1, result.size(), "Result should have one element");
    assertEquals(1, result.get(0).getBidders().size(), "Result should have one bidder");
  }
}
