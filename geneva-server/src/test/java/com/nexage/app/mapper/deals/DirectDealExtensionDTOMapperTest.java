package com.nexage.app.mapper.deals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.model.BaseTarget;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.DealRtbProfileViewUsingFormulas;
import com.nexage.admin.core.model.DealTarget;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.sparta.jpa.model.DealBidder;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealProfile;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealRule;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealBidderDTO;
import com.nexage.app.dto.deal.DealSiteDTO;
import com.nexage.app.dto.deal.RTBProfileDTO;
import com.nexage.app.dto.deals.DealPositionDTO;
import com.nexage.app.dto.deals.DealPublisherDTO;
import com.nexage.app.dto.deals.DealRuleDTO;
import com.nexage.app.dto.deals.DealTargetDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.deal.DirectDealExtensionDTOMapper;
import com.nexage.app.mapper.deal.DirectDealExtensionDTOMapperImpl;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DirectDealExtensionDTOMapperTest {

  @InjectMocks private DirectDealExtensionDTOMapperImpl directDealExtensionDTOMapper;

  @BeforeEach
  public void setup() {
    openMocks(this);
  }

  @Test
  void shouldReturnEmptyListWhenNullListOfPublishersIsPassed() {
    // given
    List<DealPublisher> publishers = null;

    // when
    List<DealPublisherDTO> returnedList =
        directDealExtensionDTOMapper.convertPublishers(publishers);

    // then
    assertTrue(returnedList.isEmpty());
  }

  @Test
  void shouldReturnPublisherDTOWhenPublishersArePassed() {
    // given
    DealPublisher dealPublisher = new DealPublisher();
    dealPublisher.setPubPid(1L);

    List<DealPublisher> publishers = List.of(dealPublisher);

    // when
    List<DealPublisherDTO> returnedList =
        directDealExtensionDTOMapper.convertPublishers(publishers);

    // then
    assertEquals(returnedList.get(0).getPid(), dealPublisher.getPid());
  }

  @Test
  void shouldReturnEmptySetWhenNullListOfDealRulesArePassed() {
    // given
    List<DealRule> rules = null;

    // when
    Set<DealRuleDTO> returnedSet = directDealExtensionDTOMapper.convertDealRules(rules);

    // then
    assertTrue(returnedSet.isEmpty());
  }

  @Test
  void shouldReturnDealRuleDTOWhenDealRuleIsPassed() {
    // given
    DealRule dealRule = new DealRule();
    dealRule.setRulePid(1L);

    List<DealRule> rules = List.of(dealRule);

    // when
    Set<DealRuleDTO> returnedSet = directDealExtensionDTOMapper.convertDealRules(rules);

    // then

    assertEquals(returnedSet.iterator().next().getRulePid(), dealRule.getRulePid());
  }

  @Test
  void shouldReturnAuctionTypeNoneWhenNullIsPassed() {
    // given
    Integer auctionType = null;

    // when
    DirectDealDTO.AuctionType returnedAuctionType =
        directDealExtensionDTOMapper.convertAuctionType(auctionType);

    // then
    assertEquals(DirectDealDTO.AuctionType.NONE, returnedAuctionType);
  }

  @Test
  void shouldReturnFromIntegerToAuctionTypeObject() {
    // given
    Integer auctionType = 1;

    // when
    DirectDealDTO.AuctionType returnedAuctionType =
        directDealExtensionDTOMapper.convertAuctionType(auctionType);

    // then
    assertEquals(DirectDealDTO.AuctionType.FIRST_PRICE, returnedAuctionType);
  }

  @Test
  void shouldReturnEmptyListWhenNullListOfDealPositionIsPassed() {
    // given
    List<DealPosition> positions = null;

    // when
    List<DealPositionDTO> returnedList =
        directDealExtensionDTOMapper.convertDealPositions(positions);

    // then
    assertTrue(returnedList.isEmpty());
  }

  @Test
  void shouldReturnDealPositionDTOWhenDealPositionIsPassed() {
    // given
    DealPosition dealPosition = new DealPosition();
    dealPosition.setPid(123L);
    List<DealPosition> positions = List.of(dealPosition);

    // when
    List<DealPositionDTO> returnedList =
        directDealExtensionDTOMapper.convertDealPositions(positions);

    // then
    assertEquals((long) returnedList.get(0).getPid(), dealPosition.getPid());
  }

  @Test
  void shouldReturnEmptyListWhenNullListOfDealSiteIsPassed() {
    // given
    List<DealSite> sites = null;

    // when
    List<DealSiteDTO> returnedList = directDealExtensionDTOMapper.convertSites(sites);

    // then
    assertTrue(returnedList.isEmpty());
  }

  @Test
  void shouldReturnDealSitesDTOFromDealSiteBeingPassed() {
    // given
    DealSite dealSite = new DealSite();
    dealSite.setSitePid(123L);

    List<DealSite> sites = List.of(dealSite);

    // when
    List<DealSiteDTO> returnedList = directDealExtensionDTOMapper.convertSites(sites);

    // then
    assertEquals(returnedList.get(0).getSitePid(), dealSite.getSitePid());
  }

  @Test
  void shouldFilterAndConvertProfilesToDtosFromDealProfilesBeingPassed()
      throws IllegalAccessException {
    // given
    DealRtbProfileViewUsingFormulas dealRtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();
    FieldUtils.writeField(dealRtbProfileViewUsingFormulas, "platform", "ANDROID", true);

    DirectDeal directDeal = new DirectDeal();
    directDeal.setPid(123L);
    directDeal.setDealId("321");

    DealProfile dealProfile = new DealProfile();
    FieldUtils.writeField(dealProfile, "pid", 111L, true);
    dealProfile.setRtbProfile(dealRtbProfileViewUsingFormulas);
    dealProfile.setDeal(directDeal);

    List<DealProfile> dealProfiles = List.of(dealProfile);

    // when
    List<RTBProfileDTO> returnedList =
        directDealExtensionDTOMapper.filterAndConvertProfilesToDtos(dealProfiles);

    // then
    assertEquals(returnedList.get(0).getPid().longValue(), dealProfile.getPid());
    assertEquals(Platform.ANDROID, returnedList.get(0).getPlatform());
  }

  @Test
  void shouldReturnEmptyListWhenNullListOfDealBiddersArePassed() {
    // given
    List<DealBidder> dealBidders = null;

    // when
    List<DealBidderDTO> returnedList = directDealExtensionDTOMapper.convertBidders(dealBidders);

    // then
    assertTrue(returnedList.isEmpty());
  }

  @Test
  void shouldConvertBiddersFromDealBiddersBeingPassed() {
    // given
    BidderConfig bidderConfig = new BidderConfig();
    bidderConfig.setPid(123L);

    DealBidder dealBidder = new DealBidder();
    dealBidder.setPid(1L);
    dealBidder.setBidderConfig(bidderConfig);
    dealBidder.setFilterSeats("testFilteredSeats");
    dealBidder.setFilterAdomains("testAdDomains");

    List<DealBidder> dealBidders = List.of(dealBidder);

    // when
    List<DealBidderDTO> returnedList = directDealExtensionDTOMapper.convertBidders(dealBidders);

    // then
    assertEquals(returnedList.get(0).getPid(), dealBidder.getPid());
    assertEquals(returnedList.get(0).getBidderPid(), dealBidder.getBidderConfig().getPid());
    assertTrue(returnedList.get(0).getWseat().get(0).contains(dealBidder.getFilterSeats()));
    assertEquals(1, returnedList.get(0).getWseat().size());
    assertTrue(returnedList.get(0).getAdomains().get(0).contains(dealBidder.getFilterAdomains()));
    assertEquals(1, returnedList.get(0).getAdomains().size());
  }

  @Test
  void shouldReturnEmptySetWhenEmptyListOfDealTargetsIsPassed() {
    // given
    Set<DealTarget> dealTargets = null;

    // when
    Set<DealTargetDTO> returnedSet = directDealExtensionDTOMapper.convertTargets(dealTargets);

    // then
    assertTrue(returnedSet.isEmpty());
  }

  @Test
  void shouldConvertTargetsFromDealTargetsBeingPassed() {
    // given
    BaseTarget.TargetType targetType = BaseTarget.TargetType.COUNTRY_STATE;

    DirectDeal directDeal = new DirectDeal();
    directDeal.setDealId("123");

    DealTarget dealTarget = new DealTarget();
    dealTarget.setDeal(directDeal);
    dealTarget.setPid(1L);
    dealTarget.setTargetType(targetType);
    dealTarget.setData("testData");
    dealTarget.setParamName("testParamName");

    Set<DealTarget> dealTargets = Set.of(dealTarget);

    // when
    Set<DealTargetDTO> returnedSet = directDealExtensionDTOMapper.convertTargets(dealTargets);

    // then
    assertEquals(returnedSet.iterator().next().getPid(), dealTarget.getPid());
    assertEquals(returnedSet.iterator().next().getTargetType(), dealTarget.getTargetType());
    assertEquals(returnedSet.iterator().next().getData(), dealTarget.getData());
    assertEquals(returnedSet.iterator().next().getParamName(), dealTarget.getParamName());
  }

  @Test
  void shouldReturnNullPlacementFormulaWhenNullStringIsPassed() {
    // given
    String nullString = null;

    // when
    PlacementFormulaDTO returnedPlacementFormulaDTO =
        DirectDealExtensionDTOMapper.placementFormulaMake(nullString);

    // then
    assertNull(returnedPlacementFormulaDTO);
  }

  @Test
  void shouldThrowExceptionOnPlacementFormulaMakeWhenIOExceptionOccur() {
    // given
    String invalidString = "";

    // when
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> DirectDealExtensionDTOMapper.placementFormulaMake(invalidString));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR, exception.getErrorCode());
  }
}
