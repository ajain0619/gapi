package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.filter.BidderConfigDenyAllowFilterList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BidderConfigTest {

  private final BidderConfig bidderConfig = new BidderConfig();

  @Test
  void shouldHandleDenyAllowFilterListsBackwardCompatibilityNullCase() {
    // when
    bidderConfig.setBidderConfigBlackWhiteFilterLists(
        new HashSet<>(List.of(new BidderConfigDenyAllowFilterList())));

    // then
    assertEquals(
        1,
        bidderConfig.getBidderConfigDenyAllowFilterLists().size(),
        "When its own value is null copies bidderConfigBlackWhiteFilterLists");
  }

  @Test
  void shouldHandleDenyAllowFilterListsBackwardCompatibilityEmptyCase() {
    // given
    bidderConfig.setBidderConfigDenyAllowFilterLists(Collections.emptySet());

    // when
    bidderConfig.setBidderConfigBlackWhiteFilterLists(
        new HashSet<>(List.of(new BidderConfigDenyAllowFilterList())));

    // then
    assertEquals(
        1,
        bidderConfig.getBidderConfigDenyAllowFilterLists().size(),
        "When its own value is empty copies bidderConfigBlackWhiteFilterLists");
  }

  @Test
  void shouldSetAllowedDeviceTypes() {
    // when
    bidderConfig.setAllowedDeviceTypes(Set.of(new BidderDeviceType()));

    // then
    assertEquals(
        1, bidderConfig.getAllowedDeviceTypes().size(), "allowedDeviceTypes size should be 1");
  }

  @Test
  void shouldRetrieveSubscriptionInfoCorrectly() {
    // given
    var bidderSubscription = new BidderSubscription();
    var externalDataProvider = new ExternalDataProvider();
    externalDataProvider.setPid(1L);
    bidderSubscription.setExternalDataProvider(externalDataProvider);
    bidderSubscription.setRequiresDataToBid(true);
    bidderSubscription.setBidderAlias("TestAlias");
    bidderConfig.setBidderSubscriptions(Set.of(bidderSubscription));

    // when
    var result = bidderConfig.getSubscriptionInfo().iterator().next();

    // then
    assertEquals(1L, result.getDataProviderPid());
    assertTrue(result.isRequiresDataToBid());
    assertEquals("TestAlias", result.getBidderAlias());
  }
}
