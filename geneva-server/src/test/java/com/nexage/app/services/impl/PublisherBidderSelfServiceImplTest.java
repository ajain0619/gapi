package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerEligibleBidders;
import com.nexage.app.dto.RuleDSPBiddersDTO;
import com.nexage.app.dto.deals.DealBidderDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.services.PublisherRtbProfileLibrarySelfService;
import com.nexage.app.services.RuleDSPService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherBidderSelfServiceImplTest {

  @Mock private RuleDSPService ruleDSPService;
  @Mock private PublisherRtbProfileLibrarySelfService publisherRtbProfileLibrarySelfService;
  @InjectMocks private PublisherBidderSelfServiceImpl publisherSelfService;

  @Test
  void shouldGetDSPsWithNoEligibleBidders() {

    // given
    Set<SellerEligibleBidders> eligibleBidders = new HashSet<>();
    Company publisher = getPublisherWithEligibleBidders(eligibleBidders);
    when(ruleDSPService.findAll()).thenReturn(getDSPs());

    // when
    List<RuleDSPBiddersDTO> result = publisherSelfService.getRuleDSPBidders(publisher.getPid());

    // then
    assertEquals(2, result.size());
    assertEquals(3, result.get(0).getBidders().size());
  }

  @Test
  void shouldGetDSPsWithSomeEligibleBidders() {
    // given
    Set<SellerEligibleBidders> eligibleBiddersWithBidderGroups =
        new HashSet<>(
            List.of(
                getEligibleBidderWithBidderGroups(3001L, new HashSet<>(List.of(6011L, 6012L))),
                getEligibleBidderWithBidderGroups(3002L, new HashSet<>(List.of(9021L, 9022L)))));
    Company publisher = getPublisherWithEligibleBidders(eligibleBiddersWithBidderGroups);
    when(publisherRtbProfileLibrarySelfService.getEligibleBidders(publisher.getPid()))
        .thenReturn(Set.of(6001L, 6002L, 4003L, 3001L, 5001L, 3002L, 5002L));
    when(ruleDSPService.findAll()).thenReturn(getDSPs());

    // when
    List<RuleDSPBiddersDTO> result = publisherSelfService.getRuleDSPBidders(1L);

    // then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(2, result.get(0).getBidders().size());
  }

  private Company getPublisherWithEligibleBidders(Set<SellerEligibleBidders> eligibleBidders) {
    Company c = new Company();
    c.setPid(1L);
    c.setName("Company-1");
    c.setId(UUID.randomUUID().toString());
    c.setEligibleBidders(eligibleBidders);
    return c;
  }

  private SellerEligibleBidders getEligibleBidderWithBidderGroups(
      long bidderPid, Set<Long> eligibleBidderGroups) {
    SellerEligibleBidders result = new SellerEligibleBidders();
    result.setPid(bidderPid);
    result.setEligibleBidderGroups(eligibleBidderGroups);

    return result;
  }

  private PublisherRTBProfileGroupDTO getPublisherRTBProfileGroup(String data) {
    PublisherRTBProfileGroupDTO result = mock(PublisherRTBProfileGroupDTO.class);
    when(result.getData()).thenReturn(data);

    return result;
  }

  private DealBidderDTO getBiddersForDSP(long dspPid, long bidderPid, String bidderName) {
    DealBidderDTO bidder = new DealBidderDTO();
    bidder.setCompanyPid(dspPid);
    bidder.setName(bidderName);
    bidder.setPid(bidderPid);

    return bidder;
  }

  private List<RuleDSPBiddersDTO> getDSPs() {
    List<RuleDSPBiddersDTO> dsps = new ArrayList<>();

    RuleDSPBiddersDTO dsp1 = new RuleDSPBiddersDTO();
    dsp1.setName("Buyer-1");
    dsp1.setPid(300L);
    dsp1.setBidders(
        new HashSet<>(
            List.of(
                getBiddersForDSP(300L, 3001L, "bidder1-for-300"),
                getBiddersForDSP(300L, 3002L, "bidder2-for-300"),
                getBiddersForDSP(300L, 3003L, "bidder3-for-300"))));

    RuleDSPBiddersDTO dsp2 = new RuleDSPBiddersDTO();
    dsp2.setName("Buyer-2");
    dsp2.setPid(301L);
    dsp2.setBidders(
        new HashSet<>(
            List.of(
                getBiddersForDSP(301L, 3011L, "bidder1-for-301"),
                getBiddersForDSP(301L, 3012L, "bidder2-for-301"))));

    dsps.add(dsp1);
    dsps.add(dsp2);

    return dsps;
  }
}
