package com.nexage.app.services.impl;

import static com.nexage.app.util.JpaPolyfills.addSortBy;
import static com.nexage.app.util.JpaPolyfills.createPageRequestWithTableAliases;

import com.nexage.admin.core.model.SiteMetrics_;
import com.nexage.admin.core.model.Site_;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.SiteMetricsAggregation;
import com.nexage.admin.dw.util.DateUtil;
import com.nexage.app.dto.pub.self.serve.PubSelfServeSummaryMetrics;
import com.nexage.app.dto.seller.SitesSummaryDTO;
import com.nexage.app.mapper.site.SiteSummaryDTOMapper;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SitesSummaryDTOService;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@PreAuthorize(
    "@loginUserContext.isOcAdminNexage() "
        + "or @loginUserContext.isOcManagerNexage() "
        + "or @loginUserContext.isOcUserNexage() "
        + "or @loginUserContext.isOcAdminSeller() "
        + "or @loginUserContext.isOcManagerSeller() "
        + "or @loginUserContext.isOcUserSeller() "
        + "or @loginUserContext.isOcManagerYieldNexage()")
public class SitesSummaryDTOServiceImpl implements SitesSummaryDTOService {
  private final SiteRepository siteRepository;

  private final UserContext userContext;

  private final LoginUserContext loginUserContext;

  private static final Map<String, String> aliasMap =
      Stream.of(
              SiteMetrics_.AD_CLICKED,
              SiteMetrics_.AD_DELIVERED,
              SiteMetrics_.AD_REQUESTED,
              SiteMetrics_.AD_SERVED,
              SiteMetrics_.CTR,
              SiteMetrics_.ECPM,
              SiteMetrics_.FILL_RATE,
              SiteMetrics_.RPM,
              SiteMetrics_.SELLER_REVENUE,
              SiteMetrics_.TOTAL_ECPM,
              SiteMetrics_.TOTAL_RPM)
          .collect(Collectors.toMap(key -> key, value -> SiteRepository.SITE_METRICS_TABLE_ALIAS));

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "not T(com.ssp.geneva.common.model.inventory.CompanyType).SELLER.equals(@loginUserContext.getType()) "
          + " or @loginUserContext.doSameOrNexageAffiliation(#sellerId)")
  public SitesSummaryDTO getSitesSummaryDTO(
      Long sellerId,
      Date start,
      Date stop,
      Optional<String> siteName,
      Optional<List<Long>> sitePids,
      Pageable pageable)
      throws ParseException {
    String[] prevdates = DateUtil.getPreviousRelativeDateRange(start, stop);
    Date prevStart = getPrevStart(prevdates);
    pageable = createPageRequestWithTableAliases(pageable, aliasMap);
    pageable = addSortBy(pageable, Site_.PID);
    Page<SiteMetricsAggregation> sitesWithCurrentMetrics;
    if (siteName.isPresent()) {
      sitesWithCurrentMetrics =
          siteRepository.aggregateMetricsWithName(start, stop, siteName.get(), sellerId, pageable);
    } else if (sitePids.filter(pids -> !pids.isEmpty()).isPresent()) {
      sitesWithCurrentMetrics =
          siteRepository.aggregateMetricsWithPids(start, stop, sellerId, sitePids, pageable);
    } else {
      sitesWithCurrentMetrics = siteRepository.aggregateMetrics(start, stop, sellerId, pageable);
    }

    return getMetricDataOnSites(sitesWithCurrentMetrics, start, stop, prevStart, sellerId);
  }

  private SitesSummaryDTO getMetricDataOnSites(
      Page<SiteMetricsAggregation> metrics, Date start, Date stop, Date prevStart, Long sellerId) {
    PubSelfServeSummaryMetrics summaryMetrics = new PubSelfServeSummaryMetrics();
    List<Long> sitesPid =
        metrics.getContent().stream()
            .map(SiteMetricsAggregation::getPid)
            .collect(Collectors.toList());
    if (!sitesPid.isEmpty()) {
      List<SiteMetricsAggregation> sellerRevenueWithPrevDates =
          siteRepository.findSellerRevenue(prevStart, start, sitesPid);
      Map<Long, SiteMetricsAggregation> prevSellerRevenues =
          sellerRevenueWithPrevDates.stream()
              .collect(Collectors.toMap(SiteMetricsAggregation::getPid, metric -> metric));
      for (int i = 0; i < metrics.getContent().size(); i++) {
        SiteMetricsAggregation currentMetrics = metrics.getContent().get(i);
        SiteMetricsAggregation prevSellerRevenue = prevSellerRevenues.get(currentMetrics.getPid());
        if (prevSellerRevenue != null) {
          double revenueTrendPercent =
              prevSellerRevenue.getSellerRevenue() > 0
                  ? (currentMetrics.getSellerRevenue() - prevSellerRevenue.getSellerRevenue())
                      / currentMetrics.getSellerRevenue()
                      * 100.0
                  : 0.0;
          currentMetrics.setRevenueTrendPercent(revenueTrendPercent);
        } else {
          currentMetrics.setRevenueTrendPercent(0.0);
        }
      }
      summaryMetrics = getTotalSummary(sellerId, start, stop, prevStart);
    }

    SitesSummaryDTO sitesSummaryDTO = new SitesSummaryDTO();
    sitesSummaryDTO.setSummary(summaryMetrics);
    sitesSummaryDTO.setSites(metrics.map(SiteSummaryDTOMapper.MAPPER::map));
    return sitesSummaryDTO;
  }

  private Date getPrevStart(String[] prevdates) throws ParseException {
    Date prevStart;
    prevStart = DateUtil.parseFromDateFormat(prevdates[0]);
    return prevStart;
  }

  private PubSelfServeSummaryMetrics getTotalSummary(
      Long sellerId, Date start, Date stop, Date prevStart) {
    PubSelfServeSummaryMetrics summaryMetrics = new PubSelfServeSummaryMetrics();
    SiteMetricsAggregation currentSummary =
        siteRepository.findSummaryByCompanyPid(start, stop, sellerId);
    if (currentSummary == null) {
      // when a seller has no sites
      return summaryMetrics;
    }
    SiteMetricsAggregation prevSummary =
        siteRepository.findSummaryByCompanyPid(prevStart, start, sellerId);
    summaryMetrics.getClicks().aggregate(currentSummary.getAdClicked(), prevSummary.getAdClicked());
    summaryMetrics.getServed().aggregate(currentSummary.getAdServed(), prevSummary.getAdServed());
    summaryMetrics
        .getRequests()
        .aggregate(currentSummary.getAdRequested(), prevSummary.getAdRequested());
    summaryMetrics
        .getDelivered()
        .aggregate(currentSummary.getAdDelivered(), prevSummary.getAdDelivered());
    summaryMetrics
        .getRevenue()
        .aggregate(currentSummary.getSellerRevenue(), prevSummary.getSellerRevenue());
    return summaryMetrics;
  }
}
