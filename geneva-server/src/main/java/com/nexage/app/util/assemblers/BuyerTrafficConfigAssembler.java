package com.nexage.app.util.assemblers;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.BidderRegionLimit;
import com.nexage.admin.core.model.BidderSubscription;
import com.nexage.app.dto.buyer.BuyerRegionLimitDTO;
import com.nexage.app.dto.buyer.BuyerTrafficConfigDTO;
import com.nexage.app.dto.buyer.BuyerTrafficConfigDTO.AuctionType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuyerTrafficConfigAssembler extends NoContextAssembler {

  private final BuyerRegionLimitAssembler buyerRegionLimitAssembler;
  private final BuyerSubscriptionAssembler buyerSubscriptionAssembler;

  @Autowired
  public BuyerTrafficConfigAssembler(
      BuyerRegionLimitAssembler buyerRegionLimitAssembler,
      BuyerSubscriptionAssembler buyerSubscriptionAssembler) {
    this.buyerRegionLimitAssembler = buyerRegionLimitAssembler;
    this.buyerSubscriptionAssembler = buyerSubscriptionAssembler;
  }

  private static final Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();

  public static final Set<String> DEFAULT_FIELDS =
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

  public BuyerTrafficConfigDTO make(BidderConfig bidderConfig) {
    return make(bidderConfig, DEFAULT_FIELDS);
  }

  public BuyerTrafficConfigDTO make(BidderConfig bidderConfig, Set<String> fields) {
    BuyerTrafficConfigDTO.BuyerTrafficConfigDTOBuilder buyerTrafficConfigBuilder =
        BuyerTrafficConfigDTO.builder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          buyerTrafficConfigBuilder.pid(bidderConfig.getPid());
          break;
        case "version":
          buyerTrafficConfigBuilder.version(bidderConfig.getVersion());
          break;
        case "trafficEnabled":
          buyerTrafficConfigBuilder.trafficEnabled(bidderConfig.isTrafficStatus());
          break;
        case "maximumQps":
          buyerTrafficConfigBuilder.maximumQps(bidderConfig.getRequestRateFilter());
          break;
        case "regionLimits":
          if (bidderConfig.getRegionLimits() != null) {
            for (BidderRegionLimit limit : bidderConfig.getRegionLimits()) {
              buyerTrafficConfigBuilder.regionLimit(buyerRegionLimitAssembler.make(limit));
            }
          }
          break;

          // The following fields are readonly and are not handled in apply
        case "auctionTypes":
          if (bidderConfig.getAuctionTypeFilter() >= 0 && bidderConfig.getAuctionTypeFilter() < 3) {
            buyerTrafficConfigBuilder.auctionTypes(
                AuctionType.values()[bidderConfig.getAuctionTypeFilter()]);
          }
          break;
        case "countryFilters":
          if (!StringUtils.isBlank(bidderConfig.getCountryFilter())) {
            buyerTrafficConfigBuilder.countryAllowlist(bidderConfig.isCountryFilterMode());
            buyerTrafficConfigBuilder.countryFilters(
                StreamSupport.stream(
                        splitter.split(bidderConfig.getCountryFilter()).spliterator(), false)
                    .collect(Collectors.toMap(Function.identity(), Function.identity())));
          }
          break;
        case "categoryFilters":
          if (!StringUtils.isBlank(bidderConfig.getCategoriesFilter())) {
            buyerTrafficConfigBuilder.categoryAllowlist(bidderConfig.isCategoriesFilterMode());
            buyerTrafficConfigBuilder.categoryFilters(
                Sets.newHashSet(splitter.split(bidderConfig.getCategoriesFilter())));
          }
          break;
        case "publisherFilters":
          if (!StringUtils.isBlank(bidderConfig.getPublishersFilter())) {
            buyerTrafficConfigBuilder.publisherAllowlist(bidderConfig.isPublishersFilterMode());
            buyerTrafficConfigBuilder.publisherFilters(
                Lists.newArrayList(splitter.split(bidderConfig.getPublishersFilter())));
          }
          break;
        case "siteFilters":
          if (!StringUtils.isBlank(bidderConfig.getSitesFilter())) {
            buyerTrafficConfigBuilder.siteAllowlist(bidderConfig.isSitesFilterMode());
            buyerTrafficConfigBuilder.siteFilters(
                Lists.newArrayList(splitter.split(bidderConfig.getSitesFilter())));
          }
          break;
        case "locationRequired":
          buyerTrafficConfigBuilder.locationRequired(bidderConfig.isLocationEnabledOnly());
          break;
        case "deviceIdRequired":
          buyerTrafficConfigBuilder.deviceIdRequired(bidderConfig.isDeviceIdentifiedOnly());
          break;
        case "bidderFormat":
          buyerTrafficConfigBuilder.bidderFormat(
              BuyerTrafficConfigDTO.BidderFormat.valueOf(bidderConfig.getFormatType().name()));
          break;
        case "allowedTraffic":
          if (!StringUtils.isBlank(bidderConfig.getAllowedTraffic())) {
            buyerTrafficConfigBuilder.allowedTraffic(
                Lists.newArrayList(splitter.split(bidderConfig.getAllowedTraffic())));
          }
          break;
        case "subscriptions":
          if (bidderConfig.getBidderSubscriptions() != null) {
            for (BidderSubscription subscription : bidderConfig.getBidderSubscriptions()) {
              buyerTrafficConfigBuilder.subscription(buyerSubscriptionAssembler.make(subscription));
            }
          }
          break;

        default:
      }
    }

    return buyerTrafficConfigBuilder.build();
  }

  public BidderConfig apply(BidderConfig bidderConfig, BuyerTrafficConfigDTO buyerTrafficConfig) {

    Set<BidderRegionLimit> regionLimits = bidderConfig.getRegionLimits();
    checkTrafficEnabledAndMaximumQpsAndRegionLimits(bidderConfig, buyerTrafficConfig, regionLimits);

    Map<Long, BuyerRegionLimitDTO> regionLimitMap = new HashMap<>();
    if (buyerTrafficConfig.getRegionLimits() != null) {
      for (BuyerRegionLimitDTO buyerRegionLimit : buyerTrafficConfig.getRegionLimits()) {
        if (buyerRegionLimit.getPid() != null) {
          regionLimitMap.put(buyerRegionLimit.getPid(), buyerRegionLimit);
        } else {
          // create a new BidderRegionLimit
          BidderRegionLimit limit =
              buyerRegionLimitAssembler.apply(new BidderRegionLimit(), buyerRegionLimit);
          limit.setBidderConfig(bidderConfig);
          regionLimits.add(limit);
        }
      }
    }

    // if a limit from BidderConfig does not exist in BuyerRegionLimits, remove it
    Iterator<BidderRegionLimit> iter = regionLimits.iterator();
    while (iter.hasNext()) {
      BidderRegionLimit limit = iter.next();
      if (limit.getPid() != null && !regionLimitMap.containsKey(limit.getPid())) {
        iter.remove();
      }
    }

    // if a limit from BidderConfig is in regionLimitMap, update it
    iter = regionLimits.iterator();
    while (iter.hasNext()) {
      BidderRegionLimit limit = iter.next();
      if (regionLimitMap.containsKey(limit.getPid())) {
        buyerRegionLimitAssembler.apply(limit, regionLimitMap.get(limit.getPid()));
      }
    }

    return bidderConfig;
  }

  private void checkTrafficEnabledAndMaximumQpsAndRegionLimits(
      BidderConfig bidderConfig,
      BuyerTrafficConfigDTO buyerTrafficConfig,
      Set<BidderRegionLimit> regionLimits) {
    if (buyerTrafficConfig.getTrafficEnabled() != null) {
      bidderConfig.setTrafficStatus(buyerTrafficConfig.getTrafficEnabled());
    }
    if (buyerTrafficConfig.getMaximumQps() != null) {
      bidderConfig.setRequestRateFilter(buyerTrafficConfig.getMaximumQps());
    }
    if (regionLimits == null) {
      regionLimits = new HashSet<>();
      bidderConfig.setRegionLimits(regionLimits);
    }
  }
}
