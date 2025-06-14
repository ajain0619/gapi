package com.nexage.app.services.impl;

import com.nexage.admin.core.model.BidderConfig;
import com.nexage.app.dto.buyer.BuyerRegionLimitDTO;
import com.nexage.app.dto.buyer.BuyerTrafficConfigDTO;
import com.nexage.app.services.BuyerAssistantService;
import com.nexage.app.services.BuyerService;
import com.nexage.app.util.assemblers.BuyerTrafficConfigAssembler;
import com.nexage.countryservice.CountryService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserBuyer()")
public class BuyerAssistantServiceImpl implements BuyerAssistantService {

  private final BuyerTrafficConfigAssembler buyerTrafficConfigAssembler;
  private final BuyerService buyerService;
  private final CountryService countryService;
  private final NamedParameterJdbcTemplate coreJdbcTemplate;
  private final NamedParameterJdbcTemplate dwJdbcTemplate;

  public BuyerAssistantServiceImpl(
      BuyerService buyerService,
      BuyerTrafficConfigAssembler buyerTrafficConfigAssembler,
      CountryService countryService,
      @Qualifier(value = "coreDS") DataSource coreDataSource,
      @Qualifier(value = "dwDS") DataSource dataWarehouseDataSource) {
    this.buyerService = buyerService;
    this.buyerTrafficConfigAssembler = buyerTrafficConfigAssembler;
    this.countryService = countryService;
    coreJdbcTemplate = new NamedParameterJdbcTemplate(coreDataSource);
    dwJdbcTemplate = new NamedParameterJdbcTemplate(dataWarehouseDataSource);
  }

  private static final String METRICS_BUSINESS =
      ""
          + "select "
          + "coalesce(round(x.rev,2), 0) as 'Revenue', "
          + "coalesce(round(x.imp,0), 0) as 'Impressions', "
          + "coalesce(case(x.imp) when 0 then 0 else round(x.rev*1000/x.imp,2) end, 0) as 'eCPM', "
          + "x.seats as 'Seats', "
          + "x.advertisers as 'Advertisers', "
          + "x.deals as 'Deals' "
          + "from "
          + "(select sum(revenue) as rev, sum(ads_delivered) as imp, count(distinct seat_id) as "
          + "seats, count(distinct advertiser) as advertisers, count(distinct deal_id) as deals "
          + "from fact_exchange_wins where bidder_id=:bidderId and start>=:startDate and "
          + "start<:stopDate) as x; ";

  private static final String METRICS_BUSINESS_LAST7DAYS =
      ""
          + "select "
          + "coalesce(round(x.rev/7,2), 0) as 'Revenue', "
          + "coalesce(round(x.imp/7,0), 0) as 'Impressions', "
          + "coalesce(case(x.imp) when 0 then 0 else round(x.rev*1000/x.imp,2) end, 0) as 'eCPM', "
          + "x.seats as 'Seats', "
          + "x.advertisers as 'Advertisers', "
          + "x.deals as 'Deals' "
          + "from "
          + "(select sum(revenue) as rev, sum(ads_delivered) as imp, count(distinct seat_id) as "
          + "seats, count(distinct advertiser) as advertisers, count(distinct deal_id) as deals "
          + "from fact_exchange_wins where bidder_id=:bidderId and start>=:startDate and "
          + "start<:stopDate) as x; ";

  private static DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#companyPid) == true")
  public Map<String, Object> getBusinessMetrics(Long companyPid, MetricInterval interval) {
    String sql = METRICS_BUSINESS;

    List<BidderConfig> bidderConfigs = buyerService.getAllBidderConfigsByCompanyPid(companyPid);
    if (bidderConfigs.isEmpty()) {
      return null;
    }

    BidderConfig bidderConfig = bidderConfigs.get(0);

    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));
    ZonedDateTime startDate = now;
    ZonedDateTime stopDate = now;

    Map<String, Object> params = new HashMap<>();
    params.put("bidderId", bidderConfig.getPid());

    switch (interval) {
      case lastmonth:
        startDate = now.truncatedTo(ChronoUnit.DAYS).minusMonths(1).withDayOfMonth(1);
        stopDate = now.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
        break;
      case month:
        startDate = now.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
        stopDate = now;
        break;
      case last7days:
        sql = METRICS_BUSINESS_LAST7DAYS;
        startDate = now.truncatedTo(ChronoUnit.DAYS).minusDays(7);
        stopDate = now.truncatedTo(ChronoUnit.DAYS);
        break;
      case yesterday:
        startDate = now.truncatedTo(ChronoUnit.DAYS).minusDays(1);
        stopDate = now.truncatedTo(ChronoUnit.DAYS);
        break;
      case today:
        startDate = now.truncatedTo(ChronoUnit.DAYS);
        stopDate = now;
        break;
    }

    log.debug("Business Metrics");
    log.debug("   companyPid : {}", companyPid);
    log.debug("   startDate  : {}", startDate);
    log.debug("   stopDate   : {}", stopDate);

    params.put("startDate", startDate.format(dateTimeFormatter));
    params.put("stopDate", stopDate.format(dateTimeFormatter));

    return dwJdbcTemplate.queryForMap(sql, params);
  }

  private BuyerTrafficConfigDTO decorateBuyerTrafficConfig(
      BuyerTrafficConfigDTO buyerTrafficConfig) {
    if (buyerTrafficConfig.getSiteFilters() != null) {
      // get the site names and replace the pid set
      Map<String, Set<Long>> params = new HashMap<>();
      Set<Long> pids = new HashSet<>();
      for (String pid : buyerTrafficConfig.getSiteFilters()) {
        pids.add(Long.valueOf(pid));
      }
      params.put("pids", pids);
      List<String> names =
          coreJdbcTemplate.queryForList(
              "select distinct case trim(coalesce(global_alias_name, \"\")) when \"\" then name else global_alias_name end from site where pid in (:pids) order by case trim(coalesce(global_alias_name, \"\")) when \"\" then name else global_alias_name end",
              params,
              String.class);
      buyerTrafficConfig.getSiteFilters().clear();
      buyerTrafficConfig.getSiteFilters().addAll(names);
    }

    if (buyerTrafficConfig.getPublisherFilters() != null) {
      // get the site names and replace the pid set
      Map<String, Set<Long>> params = new HashMap<>();
      Set<Long> pids = new HashSet<>();
      for (String pid : buyerTrafficConfig.getPublisherFilters()) {
        pids.add(Long.valueOf(pid));
      }
      params.put("pids", pids);
      List<String> names =
          coreJdbcTemplate.queryForList(
              "select distinct trim(name) from company where pid in (:pids) order by trim(name)",
              params,
              String.class);
      buyerTrafficConfig.getPublisherFilters().clear();
      buyerTrafficConfig.getPublisherFilters().addAll(names);
    }

    if (buyerTrafficConfig.getCountryFilters() != null) {
      for (Entry<String, String> entry : buyerTrafficConfig.getCountryFilters().entrySet()) {
        entry.setValue(countryService.getName(entry.getKey()));
      }
    }

    if (buyerTrafficConfig.getRegionLimits() != null) {
      for (BuyerRegionLimitDTO buyerRegionLimit : buyerTrafficConfig.getRegionLimits()) {
        for (Entry<String, String> entry : buyerRegionLimit.getCountries().entrySet()) {
          entry.setValue(countryService.getName(entry.getKey()));
        }
      }
    }
    return buyerTrafficConfig;
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#companyPid) == true")
  public BuyerTrafficConfigDTO getBuyerTrafficConfig(long companyPid) {
    List<BidderConfig> bidderConfigs = buyerService.getAllBidderConfigsByCompanyPid(companyPid);
    if (bidderConfigs.isEmpty()) {
      return null;
    }
    BidderConfig bidderConfig = bidderConfigs.get(0);
    BuyerTrafficConfigDTO buyerTrafficConfig = buyerTrafficConfigAssembler.make(bidderConfig);
    return decorateBuyerTrafficConfig(buyerTrafficConfig);
  }

  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.doSameOrNexageAffiliation(#companyPid) "
          + "and (@loginUserContext.isOcUserNexage() or @loginUserContext.isOcManagerBuyer())")
  public BuyerTrafficConfigDTO updateBuyerTrafficConfig(
      long companyPid, BuyerTrafficConfigDTO buyerTrafficConfig) {
    List<BidderConfig> bidderConfigs = buyerService.getAllBidderConfigsByCompanyPid(companyPid);
    if (bidderConfigs.isEmpty()) {
      return null;
    }
    BidderConfig bidderConfig = bidderConfigs.get(0);
    if (!bidderConfig.getVersion().equals(buyerTrafficConfig.getVersion())) {
      // throw stale data exception
      throw new StaleStateException("BuyerTrafficConfig has a different version of the data");
    }
    bidderConfig = buyerTrafficConfigAssembler.apply(bidderConfig, buyerTrafficConfig);
    buyerService.updateBidderConfig(bidderConfig.getPid(), bidderConfig, companyPid);
    return decorateBuyerTrafficConfig(buyerTrafficConfigAssembler.make(bidderConfig));
  }
}
