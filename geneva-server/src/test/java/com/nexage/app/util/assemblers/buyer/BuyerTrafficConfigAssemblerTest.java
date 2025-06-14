package com.nexage.app.util.assemblers.buyer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import com.nexage.admin.core.enums.BidderFormat;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.app.dto.buyer.BuyerTrafficConfigDTO;
import com.nexage.app.util.assemblers.BuyerRegionLimitAssembler;
import com.nexage.app.util.assemblers.BuyerSubscriptionAssembler;
import com.nexage.app.util.assemblers.BuyerTrafficConfigAssembler;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuyerTrafficConfigAssemblerTest {

  @Mock BuyerRegionLimitAssembler buyerRegionLimitAssembler;
  @Mock BuyerSubscriptionAssembler buyerSubscriptionAssembler;

  @InjectMocks BuyerTrafficConfigAssembler buyerTrafficConfigAssembler;

  @BeforeEach
  public void setUp() {
    openMocks(this);
  }

  @Spy BidderConfig model;

  @Test
  void test_make() {
    final Set<String> fields =
        Set.of(
            "pid",
            "version",
            "trafficEnabled",
            "maximumQps",
            "regionLimits",
            "auctionTypes",
            "countryFilters",
            "categoryFilters",
            "publisherFilters",
            "siteFilters",
            "locationRequired",
            "deviceIdRequired",
            "bidderFormat",
            "subscriptions",
            "allowedTraffic");

    model.setCategoriesFilter("test");
    model.setPublishersFilter("test");
    model.setCountryFilter("test");
    model.setFormatType(BidderFormat.OpenRTBv2_5);

    BuyerTrafficConfigDTO buyerTrafficConfigDTO = buyerTrafficConfigAssembler.make(model, fields);
    assertEquals(false, buyerTrafficConfigDTO.getCategoryAllowlist());
    assertNull(buyerTrafficConfigDTO.getSiteAllowlist());
    assertEquals(false, buyerTrafficConfigDTO.getPublisherAllowlist());
    assertEquals(false, buyerTrafficConfigDTO.getCountryAllowlist());

    verify(model, times(1)).getPid();
    verify(model, times(1)).getVersion();
    verify(model, times(1)).isTrafficStatus();
    verify(model, times(1)).getRequestRateFilter();
    verify(model, times(2)).getRegionLimits();
    verify(model, times(3)).getAuctionTypeFilter();
    verify(model, times(2)).getCountryFilter();
    verify(model, times(2)).getCategoriesFilter();
    verify(model, times(2)).getPublishersFilter();
    verify(model, times(1)).getSitesFilter();
    verify(model, times(1)).isLocationEnabledOnly();
    verify(model, times(1)).isDeviceIdentifiedOnly();
    verify(model, times(1)).getFormatType();
    verify(model, times(1)).getAllowedTraffic();
    verify(model, times(2)).getBidderSubscriptions();

    model.setPublishersFilterMode(true);
    buyerTrafficConfigDTO = buyerTrafficConfigAssembler.make(model, fields);
    assertEquals(true, buyerTrafficConfigDTO.getPublisherAllowlist());
    verify(model, times(4)).getPublishersFilter();
  }

  @Test
  void test_apply() {
    BuyerTrafficConfigDTO.BuyerTrafficConfigDTOBuilder buyerTrafficConfigBuilder =
        BuyerTrafficConfigDTO.builder();

    model.setRegionLimits(new HashSet<>());
    model.setRequestRateFilter(1);
    model.setTrafficStatus(false);

    buyerTrafficConfigBuilder.maximumQps(model.getRequestRateFilter());
    buyerTrafficConfigBuilder.trafficEnabled(model.isTrafficStatus());
    buyerTrafficConfigBuilder.categoryAllowlist(model.isCategoriesFilterMode());
    buyerTrafficConfigBuilder.publisherAllowlist(model.isPublishersFilterMode());
    buyerTrafficConfigBuilder.siteAllowlist(model.isSitesFilterMode());
    buyerTrafficConfigBuilder.countryAllowlist(model.isCountryFilterMode());

    BuyerTrafficConfigDTO newConfig = buyerTrafficConfigBuilder.build();

    BidderConfig appliedConfig = buyerTrafficConfigAssembler.apply(model, newConfig);

    assertEquals(model, appliedConfig);
  }
}
