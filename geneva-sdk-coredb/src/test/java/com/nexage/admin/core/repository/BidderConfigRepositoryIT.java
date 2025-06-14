package com.nexage.admin.core.repository;

import static com.nexage.admin.core.util.TestUtil.getTestBidderConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.base.Joiner;
import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.AdSizeFilter;
import com.nexage.admin.core.enums.BidderFormat;
import com.nexage.admin.core.enums.BillingSource;
import com.nexage.admin.core.enums.BuyerDomainVerificationAuthLevel;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.IdentityProvider;
import com.nexage.admin.core.model.IdentityProviderView;
import com.nexage.admin.core.model.filter.BidderConfigDenyAllowFilterList;
import com.nexage.admin.core.model.filter.FilterList;
import com.nexage.admin.core.model.filter.FilterListType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/bidder-config-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BidderConfigRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final long NON_EXISTENT_PID = 999L;
  private static final String NON_EXISTENT_SEAT = "seat99";

  @Autowired private BidderConfigRepository bidderConfigRepository;
  @Autowired private CompanyRepository companyRepository;
  @Autowired private FilterListRepository filterListRepository;
  @Autowired private IdentityProviderRepository identityProviderRepository;

  @Test
  void shouldUpdateBidderConfig() {
    // given
    BidderConfig c = bidderConfigRepository.findById(100L).orElseThrow();
    c.setDefaultBidCurrency("EUR");
    c.setBillingSource(BillingSource.BRXD);
    c.setRequestRateFilter(10);
    c.setAllowBridgeIdMatch(!c.getAllowBridgeIdMatch());
    c.setAllowConnectId(!c.getAllowConnectId());
    c.setAllowLiveramp(!c.getAllowLiveramp());
    c.setAllowIdGraphMatch(!c.getAllowIdGraphMatch());
    c.setSendDealSizes(!c.getSendDealSizes());
    c.setDomainVerificationAuthLevel(BuyerDomainVerificationAuthLevel.ALLOW_ALL);

    // when
    BidderConfig updated = bidderConfigRepository.save(c);

    // then
    assertNotNull(updated);
    assertNotNull(updated.getId());
    BidderConfig dbData = bidderConfigRepository.findById(updated.getPid()).orElseThrow();
    assertNotNull(dbData);
    assertNotNull(dbData.getId());
    assertEquals("EUR", dbData.getDefaultBidCurrency());
    assertEquals(BillingSource.BRXD, c.getBillingSource());
    assertFalse(dbData.getAllowBridgeIdMatch());
    assertFalse(dbData.getAllowConnectId());
    assertFalse(dbData.getAllowLiveramp());
    assertFalse(dbData.getAllowIdGraphMatch());
    assertFalse(dbData.getSendDealSizes());
    assertEquals(
        BuyerDomainVerificationAuthLevel.ALLOW_ALL, dbData.getDomainVerificationAuthLevel());
  }

  @Test
  void shouldCountBuyerGroupsWhenBidderExistsAndBuyerGroupExists() {
    // given
    var bidderPid = 100L;
    var buyerGroupPid = 1L;

    // when
    long count =
        bidderConfigRepository.countByPidAndCompany_buyerGroups_pidIn(
            bidderPid, Set.of(buyerGroupPid));

    // then
    assertEquals(1, count);
  }

  @Test
  void shouldReturnZeroBuyerGroupCountWhenBidderDoesNotExistAndBuyerGroupExists() {
    // given
    var buyerGroupPid = 1L;

    // when
    long count =
        bidderConfigRepository.countByPidAndCompany_buyerGroups_pidIn(
            NON_EXISTENT_PID, Set.of(buyerGroupPid));

    // then
    assertEquals(0, count);
  }

  @Test
  void shouldReturnZeroBuyerGroupCountWhenBidderExistsAndBuyerGroupDoesNotExist() {
    // given
    var bidderPid = 100L;

    // when
    long count =
        bidderConfigRepository.countByPidAndCompany_buyerGroups_pidIn(
            bidderPid, Set.of(NON_EXISTENT_PID));

    // then
    assertEquals(0, count);
  }

  @Test
  void shouldCountBuyerSeatsWhenBidderExistsAndBuyerSeatExists() {
    // given
    var bidderPid = 100L;
    var seat = "seat1";

    // when
    long count =
        bidderConfigRepository.countByPidAndCompany_buyerSeats_seatIn(bidderPid, Set.of(seat));

    // then
    assertEquals(1, count);
  }

  @Test
  void shouldReturnZeroBuyerSeatCountWhenBidderDoesNotExistAndBuyerSeatExists() {
    // given
    var seat = "seat1";

    // when
    long count =
        bidderConfigRepository.countByPidAndCompany_buyerSeats_seatIn(
            NON_EXISTENT_PID, Set.of(seat));

    // then
    assertEquals(0, count);
  }

  @Test
  void shouldReturnZeroBuyerSeatCountWhenBidderExistsAndBuyerSeatDoesNotExist() {
    // given
    var bidderPid = 100L;

    // when
    long count =
        bidderConfigRepository.countByPidAndCompany_buyerSeats_seatIn(
            bidderPid, Set.of(NON_EXISTENT_SEAT));

    // then
    assertEquals(0, count);
  }

  @Test
  void shouldFindAllBidderConfigs() {
    // given
    final Set<Long> expectedBidderConfigPids = Set.of(100L, 101L, 102L, 103L);

    // when
    List<BidderConfig> returnedBidderConfigs = bidderConfigRepository.findAll();

    // then
    assertEquals(
        expectedBidderConfigPids,
        returnedBidderConfigs.stream().map(BidderConfig::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindBidderConfigsByCompanyPid() {
    // given
    final Set<Long> expectedBidderConfigPids = Set.of(100L, 101L);

    // when
    List<BidderConfig> returnedBidderConfigs = bidderConfigRepository.findByCompanyPid(300L);

    // then
    assertEquals(
        expectedBidderConfigPids,
        returnedBidderConfigs.stream().map(BidderConfig::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindBidderConfigByPid() {
    // when
    BidderConfig bidderConfig = bidderConfigRepository.findById(100L).orElseThrow();

    // then
    assertNotNull(bidderConfig);
    assertNotNull(bidderConfig.getName());
    assertEquals("DEFAULTNAME", bidderConfig.getName());
    assertTrue(bidderConfig.getAllowBridgeIdMatch());
    assertTrue(bidderConfig.getAllowConnectId());
    assertTrue(bidderConfig.getAllowLiveramp());
    assertTrue(bidderConfig.getSendDealSizes());
  }

  @Test
  void shouldFindBidderConfigByPidOf251Format() {
    // when
    BidderConfig bidderConfig = bidderConfigRepository.findById(103L).orElseThrow();

    // then
    assertNotNull(bidderConfig);
    assertNotNull(bidderConfig.getName());
    assertEquals(BidderFormat.OpenRTBv2_5_1, bidderConfig.getFormatType());
  }

  @Test
  void shouldUpdateBidderConfigTrafficType() {
    // given
    BidderConfig bidderConfig = bidderConfigRepository.findById(100L).orElseThrow();
    Set<Type> allowedTypes = getAllowedTrafficTypes(bidderConfig.getAllowedTraffic());
    allowedTypes.add(Type.APPLICATION);
    allowedTypes.add(Type.MOBILE_WEB);
    bidderConfig.setAllowedTraffic(setAllowedTrafficTypes(allowedTypes));

    // when
    BidderConfig updated = bidderConfigRepository.save(bidderConfig);

    // then
    assertNotNull(updated);
    allowedTypes = getAllowedTrafficTypes(updated.getAllowedTraffic());
    assertEquals(2, allowedTypes.size(), "Allowed types must contain 2 entries");
    assertTrue(allowedTypes.contains(Type.APPLICATION), "Allowed types must have APPLICATION");
    assertTrue(allowedTypes.contains(Type.MOBILE_WEB), "Allowed types must have MOBILE_WEB");
  }

  @Test
  void shouldReturnEmptyListOnFindByCompanyPidWhenBidderConfigDoesNotExist() {
    // when
    List<BidderConfig> bidderConfigs = bidderConfigRepository.findByCompanyPid(NON_EXISTENT_PID);

    // then
    assertTrue(bidderConfigs.isEmpty());
  }

  @Test
  void shouldSaveBidderConfigWithEmptyFiltersWhenFiltersAreBlank() {
    // given
    BidderConfig bidderConfig = bidderConfigRepository.findById(100L).orElseThrow();
    bidderConfig.setCategoriesFilter("");
    bidderConfig.setDevicesFilter("    ");
    bidderConfig.setCountryFilter("");
    bidderConfig.setAdSizesFilter(null);

    // when
    bidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNull(bidderConfig.getCategoriesFilter());
    assertNull(bidderConfig.getDevicesFilter());
    assertNull(bidderConfig.getCountryFilter());
    assertTrue(bidderConfig.getAdSizesFilter().isEmpty());
  }

  @Test
  void shouldSaveBidderConfigWithFilters() {
    // given
    BidderConfig bidderConfig = bidderConfigRepository.findById(100L).orElseThrow();
    bidderConfig.setCategoriesFilter("IAB1, IAB2");
    bidderConfig.setDevicesFilter("SamsungGalaxyS8,Nexus5X");
    bidderConfig.setCountryFilter("BDI,COM,DJI,ERI");
    Set<AdSizeFilter> filters = new HashSet<>();
    filters.add(AdSizeFilter._300_X_250);
    filters.add(AdSizeFilter._320_X_50);
    filters.add(AdSizeFilter._320_X_480);
    filters.add(AdSizeFilter._480_X_320);
    filters.add(AdSizeFilter._728_X_90);
    filters.add(AdSizeFilter._950_X90);
    filters.add(AdSizeFilter._980_X_90);
    filters.add(AdSizeFilter._250_X_360);
    filters.add(AdSizeFilter._1000_X_200);
    filters.add(AdSizeFilter._360_X_200);
    filters.add(AdSizeFilter._300_X_200);
    filters.add(AdSizeFilter._1920_X_420);
    filters.add(AdSizeFilter._768_X_720);
    filters.add(AdSizeFilter._320_X_180);
    bidderConfig.setAdSizesFilter(filters);
    bidderConfig.setAdSizesFilterMode(true);

    // when
    bidderConfig = bidderConfigRepository.save(bidderConfig);

    // then
    assertEquals("IAB1, IAB2", bidderConfig.getCategoriesFilter());
    assertEquals("SamsungGalaxyS8,Nexus5X", bidderConfig.getDevicesFilter());
    assertEquals("BDI,COM,DJI,ERI", bidderConfig.getCountryFilter());
    assertEquals(filters, bidderConfig.getAdSizesFilter());
    assertTrue(bidderConfig.isAdSizesFilterMode());
  }

  @Test
  void shouldFindBidderPidsWhenValidCompanyAsInputAndBidderExists() {
    // given
    final Set<Long> expectedBidderConfigPids = Set.of(100L, 101L);

    // when
    Set<Long> bidderPids = bidderConfigRepository.findPidsByCompanyPid(300L);

    // then
    assertEquals(expectedBidderConfigPids, bidderPids);
  }

  @Test
  void shouldReturnEmptySetOnFindBidderPidsWhenInvalidCompanyAsInput() {
    // when
    Set<Long> bidderPids = bidderConfigRepository.findPidsByCompanyPid(NON_EXISTENT_PID);

    // then
    assertEquals(0, bidderPids.size(), "bidder count must be zero");
  }

  @Test
  void shouldSaveBidderConfigWithBlackWhiteFilterListWhenOnlyBidderConfigDenyAllowFilterListsSet() {
    // given
    BidderConfig bidderConfig = getTestBidderConfig();
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2);
    FilterList filterList = createTestFilterList();
    filterList.setPid(1);
    filterListRepository.save(filterList);
    BidderConfigDenyAllowFilterList bidderConfigDenyAllowFilterList =
        new BidderConfigDenyAllowFilterList();
    bidderConfigDenyAllowFilterList.setPid(1);
    bidderConfigDenyAllowFilterList.setFilterList(filterList);
    bidderConfigDenyAllowFilterList.setFilterListNonInclusive(filterList);
    bidderConfigDenyAllowFilterList.setBidderConfig(bidderConfig);
    bidderConfig.setBidderConfigDenyAllowFilterLists(
        new HashSet<>(Arrays.asList(bidderConfigDenyAllowFilterList)));

    // when
    bidderConfig = bidderConfigRepository.save(bidderConfig);

    // then
    assertEquals(1, bidderConfig.getBidderConfigDenyAllowFilterLists().size());
    assertEquals(
        bidderConfigDenyAllowFilterList,
        bidderConfig.getBidderConfigDenyAllowFilterLists().toArray()[0]);
    assertEquals(1, bidderConfig.getBidderConfigBlackWhiteFilterLists().size());
    // maintain backward compatibility
    assertEquals(
        bidderConfigDenyAllowFilterList,
        bidderConfig.getBidderConfigBlackWhiteFilterLists().toArray()[0]);
  }

  @Test
  void shouldSaveBidderConfigToDbWhenAllowBridgeIdIsTrue() {
    // given
    BidderConfig bidderConfig = new BidderConfig();
    String bidderConfigId = UUID.randomUUID().toString().substring(0, 31);
    bidderConfig.setId(bidderConfigId);
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2);
    bidderConfig.setBidRequestCpm(BigDecimal.valueOf(0.0005));
    bidderConfig.setRequestRateFilter(0);
    bidderConfig.setAllowBridgeIdMatch(true);

    // when
    BidderConfig dbBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNotNull(dbBidderConfig);
    assertEquals(bidderConfigId, dbBidderConfig.getId());
    assertTrue(dbBidderConfig.getAllowBridgeIdMatch());
  }

  @Test
  void shouldSaveBidderConfigToDbWhenAllowConnectIdSet() {
    // given
    BidderConfig bidderConfig = getTestBidderConfig();
    bidderConfig.setId(UUID.randomUUID().toString().substring(0, 31));
    bidderConfig.setAllowConnectId(true);

    // when
    BidderConfig dbBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNotNull(dbBidderConfig);
    assertEquals(bidderConfig.getId(), dbBidderConfig.getId());
    assertEquals(bidderConfig.getAllowConnectId(), dbBidderConfig.getAllowConnectId());
  }

  @Test
  void shouldSaveBidderConfigToDbWithDefaultValueWhenAllowConnectIdNotSet() {
    // given
    BidderConfig bidderConfig = getTestBidderConfig();
    bidderConfig.setId(UUID.randomUUID().toString().substring(0, 31));

    // when
    BidderConfig dbBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNotNull(dbBidderConfig);
    assertEquals(bidderConfig.getId(), dbBidderConfig.getId());
    assertFalse(dbBidderConfig.getAllowConnectId());
  }

  @Test
  void shouldSaveBidderConfigToDbWhenAllowLiverampSet() {
    // given
    BidderConfig bidderConfig = getTestBidderConfig();
    bidderConfig.setId(UUID.randomUUID().toString().substring(0, 31));
    bidderConfig.setAllowLiveramp(true);

    // when
    BidderConfig dbBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNotNull(dbBidderConfig);
    assertEquals(bidderConfig.getId(), dbBidderConfig.getId());
    assertEquals(bidderConfig.getAllowLiveramp(), dbBidderConfig.getAllowLiveramp());
    assertTrue(dbBidderConfig.getAllowLiveramp());
  }

  @Test
  void shouldSaveBidderConfigToDbWithDefaultValueWhenAllowLiverampNotSet() {
    // given
    BidderConfig bidderConfig = getTestBidderConfig();
    bidderConfig.setId(UUID.randomUUID().toString().substring(0, 31));

    // when
    BidderConfig dbBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNotNull(dbBidderConfig);
    assertEquals(bidderConfig.getId(), dbBidderConfig.getId());
    assertFalse(dbBidderConfig.getAllowLiveramp());
  }

  @Test
  void shouldSaveBidderConfigToDbWhenAllowIdGraphMatchSet() {
    // given
    BidderConfig bidderConfig = getTestBidderConfig();
    bidderConfig.setId(UUID.randomUUID().toString().substring(0, 31));
    bidderConfig.setAllowIdGraphMatch(true);

    // when
    BidderConfig dbBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNotNull(dbBidderConfig);
    assertEquals(bidderConfig.getId(), dbBidderConfig.getId());
    assertEquals(bidderConfig.getAllowIdGraphMatch(), dbBidderConfig.getAllowIdGraphMatch());
  }

  @Test
  void shouldSaveBidderConfigToDbWithDefaultValueWhenAllowIdGraphMatchNotSet() {
    // given
    BidderConfig bidderConfig = getTestBidderConfig();
    bidderConfig.setId(UUID.randomUUID().toString().substring(0, 31));

    // when
    BidderConfig dbBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNotNull(dbBidderConfig);
    assertEquals(bidderConfig.getId(), dbBidderConfig.getId());
    assertTrue(dbBidderConfig.getAllowIdGraphMatch());
  }

  @Test
  void shouldSaveBidderConfigToDbWhenSendDealSizesIsTrue() {
    // given
    BidderConfig bidderConfig = new BidderConfig();
    String bidderConfigId = UUID.randomUUID().toString().substring(0, 31);
    bidderConfig.setId(bidderConfigId);
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2);
    bidderConfig.setBidRequestCpm(BigDecimal.valueOf(0.0005));
    bidderConfig.setRequestRateFilter(0);
    bidderConfig.setSendDealSizes(true);

    // when
    BidderConfig dbBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNotNull(dbBidderConfig);
    assertEquals(bidderConfigId, dbBidderConfig.getId());
    assertTrue(dbBidderConfig.getSendDealSizes());
  }

  @Test
  void shouldSaveBidderConfigToDbWhenDomainVerificationAuthLevelIsAllowAll() {
    // given
    BidderConfig bidderConfig = new BidderConfig();
    String bidderConfigId = UUID.randomUUID().toString().substring(0, 31);
    bidderConfig.setId(bidderConfigId);
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2);
    bidderConfig.setBidRequestCpm(BigDecimal.valueOf(0.0005));
    bidderConfig.setRequestRateFilter(0);
    bidderConfig.setDomainVerificationAuthLevel(BuyerDomainVerificationAuthLevel.ALLOW_ALL);

    // when
    BidderConfig dbBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNotNull(dbBidderConfig);
    assertEquals(bidderConfigId, dbBidderConfig.getId());
    assertEquals(
        BuyerDomainVerificationAuthLevel.ALLOW_ALL,
        dbBidderConfig.getDomainVerificationAuthLevel());
  }

  @Test
  void shouldSaveBidderConfigToDbWhenDomainVerificationAuthLevelIsAllowAuthorized() {
    // given
    BidderConfig bidderConfig = new BidderConfig();
    String bidderConfigId = UUID.randomUUID().toString().substring(0, 31);
    bidderConfig.setId(bidderConfigId);
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2);
    bidderConfig.setBidRequestCpm(BigDecimal.valueOf(0.0005));
    bidderConfig.setRequestRateFilter(0);
    bidderConfig.setDomainVerificationAuthLevel(BuyerDomainVerificationAuthLevel.ALLOW_AUTHORIZED);

    // when
    BidderConfig dbBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNotNull(dbBidderConfig);
    assertEquals(bidderConfigId, dbBidderConfig.getId());
    assertEquals(
        BuyerDomainVerificationAuthLevel.ALLOW_AUTHORIZED,
        dbBidderConfig.getDomainVerificationAuthLevel());
  }

  @Test
  void shouldSaveBidderConfigToDbWhenDomainVerificationAuthLevelIsAllowAuthorizedUncategorized() {
    // given
    BidderConfig bidderConfig = new BidderConfig();
    String bidderConfigId = UUID.randomUUID().toString().substring(0, 31);
    bidderConfig.setId(bidderConfigId);
    bidderConfig.setFormatType(BidderFormat.OpenRTBv2);
    bidderConfig.setBidRequestCpm(BigDecimal.valueOf(0.0005));
    bidderConfig.setRequestRateFilter(0);
    bidderConfig.setDomainVerificationAuthLevel(
        BuyerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_UNCATEGORIZED);

    // when
    BidderConfig dbBidderConfig = bidderConfigRepository.saveAndFlush(bidderConfig);

    // then
    assertNotNull(dbBidderConfig);
    assertEquals(bidderConfigId, dbBidderConfig.getId());
    assertEquals(
        BuyerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_UNCATEGORIZED,
        dbBidderConfig.getDomainVerificationAuthLevel());
  }

  @Test
  void shouldReturnCompanyPidWhenBidderExists() {
    // given
    var bidderPid = 100L;
    var expectedBuyerCompanyPid = 300L;

    // when
    Long companyPid = bidderConfigRepository.findCompanyPidByPid(bidderPid);

    // then
    assertEquals(expectedBuyerCompanyPid, companyPid);
  }

  @Test
  void shouldReturnBidderPidWhenBidderExistsForBuyerCompany() {
    // given
    final Long companyPid = 300L;
    final Set<Long> expectedBidderConfigPids = Set.of(100L, 101L);

    // when
    Set<Long> returnedPids = bidderConfigRepository.findPidsByCompanyPid(companyPid);

    // then
    assertEquals(expectedBidderConfigPids, returnedPids);
  }

  @Test
  void shouldReturnNullCompanyPidWhenBidderDoesNotExist() {
    // when
    Long companyPid = bidderConfigRepository.findCompanyPidByPid(NON_EXISTENT_PID);

    // then
    assertNull(companyPid);
  }

  @Test
  void shouldUpdateBidderConfigIdentityProvidersWithAdditionalIdentityProvider() {
    // given
    BidderConfig bidderConfig = bidderConfigRepository.findById(100L).orElseThrow();
    Set<IdentityProviderView> identityProviderViews = bidderConfig.getIdentityProviders();
    identityProviderViews.add(new IdentityProviderView(1L));
    bidderConfig.setIdentityProviders(identityProviderViews);

    // when
    BidderConfig updated = bidderConfigRepository.save(bidderConfig);

    // then
    List<IdentityProvider> identityProvidersList = identityProviderRepository.findAll();
    assertNotNull(updated);
    assertEquals(3, bidderConfig.getIdentityProviders().size());
    assertEquals(4, identityProvidersList.size());
  }

  @Test
  void shouldUpdateBidderConfigIdentityProvidersRemoveIdentityProvider() {
    // given
    BidderConfig bidderConfig = bidderConfigRepository.findById(100L).orElseThrow();
    Set<IdentityProviderView> identityProviderViews = bidderConfig.getIdentityProviders();
    identityProviderViews.remove(new IdentityProviderView(3L));
    bidderConfig.setIdentityProviders(identityProviderViews);

    // when
    BidderConfig updated = bidderConfigRepository.save(bidderConfig);

    // then
    List<IdentityProvider> identityProvidersList = identityProviderRepository.findAll();
    assertNotNull(updated);
    assertEquals(1, bidderConfig.getIdentityProviders().size());
    assertEquals(4, identityProvidersList.size());
  }

  private Set<Type> getAllowedTrafficTypes(String allowedTraffic) {
    Set<Type> types = new HashSet<Type>();
    if (!StringUtils.isEmpty(allowedTraffic)) {
      String[] typeStr = allowedTraffic.split(",");
      for (String typ : typeStr) {
        try {
          types.add(Type.valueOf(typ.toUpperCase()));
        } catch (IllegalArgumentException ignored) {
        }
      }
    }
    return types;
  }

  private String setAllowedTrafficTypes(Set<Type> allowedTrafficTypes) {
    String allowedTraffic;
    if (allowedTrafficTypes == null || allowedTrafficTypes.isEmpty()) {
      allowedTraffic = null;
    } else {
      List<String> typeList = new ArrayList<String>();
      for (Type typ : allowedTrafficTypes) {
        typeList.add(typ.name());
      }
      Collections.sort(typeList);
      allowedTraffic = Joiner.on(",").join(typeList);
    }
    return allowedTraffic;
  }

  private FilterList createTestFilterList() {
    FilterList filterList = new FilterList();
    filterList.setName("TestFilterListName");
    filterList.setType(FilterListType.DOMAIN);
    filterList.setCompanyId(NON_EXISTENT_PID);
    return filterList;
  }
}
