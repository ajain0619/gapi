package com.nexage.app.mapper.deals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.DealTarget;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.sparta.jpa.model.DealBidder;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealProfile;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealRule;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.mapper.deal.DirectDealDTOMapperWithoutSuppliersAndBidders;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DirectDealDTOMapperWithoutSuppliersAndBiddersTest {

  @Test
  void shouldNotMapPlacementFormulaOrProfilesWhenMappingUsingGetAllDealsMapper() {
    // given
    List<DirectDeal> directDealList = TestObjectsFactory.gimme(1, DirectDeal.class);
    DirectDeal directDeal = directDealList.get(0);

    DealProfile dealProfile = new DealProfile();
    dealProfile.setDeal(directDeal);
    List<DealProfile> dealProfiles = List.of(dealProfile);

    DealPublisher dealPublisher = new DealPublisher();
    dealPublisher.setPubPid(111L);
    List<DealPublisher> dealPublishers = List.of(dealPublisher);

    DealRule dealRule = new DealRule();
    dealRule.setRulePid(222L);
    List<DealRule> dealRules = List.of(dealRule);

    DealPosition dealPosition = new DealPosition();
    dealPosition.setPositionPid(333L);
    List<DealPosition> dealPositions = List.of(dealPosition);

    DealSite dealSite = new DealSite();
    dealSite.setSitePid(444L);
    List<DealSite> dealSites = List.of(dealSite);

    BidderConfig bidderConfig = new BidderConfig();
    bidderConfig.setPid(555L);
    DealBidder dealBidder = new DealBidder();
    dealBidder.setPid(666L);
    dealBidder.setBidderConfig(bidderConfig);
    List<DealBidder> dealBidders = List.of(dealBidder);

    DirectDeal directDealForTarget = new DirectDeal();
    directDealForTarget.setDealId("888");
    DealTarget dealTarget = new DealTarget();
    dealTarget.setPid(777L);
    dealTarget.setDeal(directDealForTarget);
    Set<DealTarget> dealTargetSet = Set.of(dealTarget);

    directDeal.setPlacementFormula("test");
    directDeal.setProfiles(dealProfiles);
    directDeal.setPublishers(dealPublishers);
    directDeal.setRules(dealRules);
    directDeal.setAuctionType(1);
    directDeal.setPositions(dealPositions);
    directDeal.setSites(dealSites);
    directDeal.setCreationDate(new Date());
    directDeal.setBidders(dealBidders);
    directDeal.setDealTargets(dealTargetSet);

    // when
    DirectDealDTO directDealDTO =
        DirectDealDTOMapperWithoutSuppliersAndBidders.MAPPER.map(directDeal);

    // then
    assertEquals(directDealDTO.getPid(), directDeal.getPid());
    assertEquals(directDealDTO.getDescription(), directDeal.getDescription());
    assertNull(directDealDTO.getPlacementFormula());
    assertTrue(directDealDTO.getProfiles().isEmpty());
    assertNull(directDealDTO.getAutoUpdate());
    assertEquals(
        directDealDTO.getSellers().get(0).getPublisherPid(), dealPublishers.get(0).getPubPid());
    assertEquals(
        directDealDTO.getRules().iterator().next().getRulePid(), dealRules.get(0).getRulePid());
    assertEquals(DirectDealDTO.AuctionType.FIRST_PRICE, directDealDTO.getAuctionType());
    assertEquals(
        directDealDTO.getPositions().get(0).getPositionPid(),
        dealPositions.get(0).getPositionPid());
    assertEquals(directDealDTO.getSites().get(0).getSitePid(), dealSites.get(0).getSitePid());
    assertEquals(directDealDTO.getCreationDate(), directDeal.getCreationDate());
    assertEquals(directDealDTO.getBidders().get(0).getPid(), dealBidders.get(0).getPid());
    assertEquals(
        directDealDTO.getBidders().get(0).getBidderPid(),
        dealBidders.get(0).getBidderConfig().getPid());
    assertEquals(
        directDealDTO.getTargets().iterator().next().getPid(),
        directDeal.getDealTargets().iterator().next().getPid());
  }
}
