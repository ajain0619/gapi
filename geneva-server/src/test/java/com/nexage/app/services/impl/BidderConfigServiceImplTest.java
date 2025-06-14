package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.dto.BidderSummaryDTO;
import com.nexage.admin.core.enums.UserIdBidRequestType;
import com.nexage.admin.core.enums.UserIdPreference;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.ExchangeRegional;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.ExchangeRegionalRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BidderConfigServiceImplTest {

  @Mock private BidderConfigRepository bidderConfigRepository;
  @Mock private ExchangeRegionalRepository exchangeRegionalRepository;
  @Mock private EntityManager entityManager;

  @InjectMocks private BidderConfigServiceImpl bidderConfigService;

  @Test
  void shouldReturnCorrectBidderSummeryDTOs() {
    // given
    List<BidderConfig> bidderConfigs =
        LongStream.range(0, 3)
            .mapToObj(i -> getBidderConfig(i, 100 + i))
            .collect(Collectors.toList());
    when(bidderConfigRepository.findByTrafficStatus(true)).thenReturn(bidderConfigs);

    // when
    List<BidderSummaryDTO> bidderSummaryDTOS = bidderConfigService.getBidderSummaries();

    // then
    assertEquals(3, bidderSummaryDTOS.size());
    for (int i = 0; i < bidderSummaryDTOS.size(); i++) {
      verifyBidderSummaryDTO(bidderConfigs.get(i), bidderSummaryDTOS.get(i));
    }
  }

  @Test
  void shouldSetDefaultExchangeRegionalWhenCreateDefaultBidderConfigForBuyer() {
    // given
    var exchangeRegionalPid = 1L;
    var exchangeRegionalId = "id123";
    var companyPid = 2L;
    var exchangeRegional = new ExchangeRegional(exchangeRegionalPid, exchangeRegionalId);
    when(exchangeRegionalRepository.findById(exchangeRegionalPid))
        .thenReturn(Optional.of(exchangeRegional));
    var company = new Company();
    company.setPid(companyPid);

    // when
    BidderConfig bc = bidderConfigService.createDefaultBidderConfigForBuyer(company);

    // then
    assertTrue(bc.getExchangeRegionals().contains(exchangeRegional));
  }

  @Test
  void shouldCreateCorrectDefaultBidderConfigForBuyer() {
    // given
    Company company = new Company();
    company.setPid(1L);
    company.setCurrency("XYZ");

    // when
    BidderConfig bidderConfig = bidderConfigService.createDefaultBidderConfigForBuyer(company);

    // then
    assertEquals(
        UserIdPreference.NO_ID_RESTRICTION,
        bidderConfig.getUserIdPreference(),
        "UserIdPreference should default to NO_ID_RESTRICTION");
    assertEquals(
        UserIdBidRequestType.UNKNOWN,
        bidderConfig.getUserIdBidRequestType(),
        "UserIdBidRequestType should default to UNKNOWN");
    assertFalse(bidderConfig.getHeaderBiddingEnabled(), "Header Bidding must be false");
    assertFalse(bidderConfig.getSendDealSizes(), "Send deal sizes must be false");
    assertFalse(bidderConfig.isCountryFilterMode(), "countryFilterMode must be false");
    assertFalse(bidderConfig.isCategoriesFilterMode(), "categoriesFilterMode must be false");
    assertFalse(bidderConfig.isDevicesFilterMode(), "devicesFilterMode must be false");
    assertFalse(bidderConfig.isAdSizesFilterMode(), "adSizesFilterMode must be false");
    assertFalse(bidderConfig.isSitesFilterMode(), "sitesFilterMode must be false");
    assertEquals(
        "XYZ", bidderConfig.getDefaultBidCurrency(), "Default Bid Currency should match Company's");
  }

  @Test
  void shouldSetIncludeListsNameToAdaptiveOnCreateDefaultBidderConfigForBuyer() {
    // given
    Company company = new Company();
    company.setPid(1L);

    // when
    BidderConfig bidderConfig = bidderConfigService.createDefaultBidderConfigForBuyer(company);

    // then
    assertEquals("ADAPTIVE", bidderConfig.getIncludeLists().name());
  }

  @Test
  void shouldSetDenyAllowFilterListsDefaultOnCreateDefaultBidderConfigForBuyer() {
    // given
    Company company = new Company();
    company.setPid(1L);

    // when
    BidderConfig bidderConfig = bidderConfigService.createDefaultBidderConfigForBuyer(company);

    // then
    assertEquals(
        0, bidderConfig.getBidderConfigDenyAllowFilterLists().size(), "Default size is empty");
  }

  @Test
  void shouldSaveFlushAndRefreshBidderConfig() {
    // given
    var bidderConfig = new BidderConfig();
    when(bidderConfigRepository.saveAndFlush(bidderConfig)).thenReturn(bidderConfig);

    // when
    bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig);

    // then
    verify(bidderConfigRepository).saveAndFlush(bidderConfig);
    verify(entityManager).refresh(bidderConfig);
  }

  private BidderConfig getBidderConfig(Long pid, Long companyPid) {
    BidderConfig bc = new BidderConfig();
    bc.setPid(pid);
    bc.setName(String.format("bidder-config-%d", pid));
    bc.setCompanyPid(companyPid);
    return bc;
  }

  private void verifyBidderSummaryDTO(BidderConfig config, BidderSummaryDTO summary) {
    assertEquals(config.getPid(), summary.getPid());
    assertEquals(config.getName(), summary.getName());
    assertEquals(config.getCompanyPid(), summary.getBuyerPid());
  }
}
