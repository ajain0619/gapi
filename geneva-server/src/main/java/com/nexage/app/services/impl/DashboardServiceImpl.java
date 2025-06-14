package com.nexage.app.services.impl;

import static java.util.Collections.emptySet;

import com.nexage.admin.core.pubselfserve.SitePubSelfServeView;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.DashboardService;
import com.nexage.dw.geneva.dashboard.DashboardFacade;
import com.nexage.dw.geneva.dashboard.model.DashboardMetric;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.server.report.performance.pss.dao.PubSelfServeMetricsDao;
import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@PreAuthorize(
    "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcUserBuyer()")
public class DashboardServiceImpl implements DashboardService {

  private final DashboardFacade dashboardFacade;
  private final PubSelfServeMetricsDao dw;
  private final UserContext userContext;

  public DashboardServiceImpl(
      @Qualifier("genevaDashboardFacade") DashboardFacade dashboardFacade,
      PubSelfServeMetricsDao dw,
      UserContext userContext) {
    this.dashboardFacade = dashboardFacade;
    this.dw = dw;
    this.userContext = userContext;
  }

  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserBuyer()")
  @Override
  public DashboardMetric getBuyerDashboard(Date start, Date stop, boolean trend) {
    if (!isTimeIntervalValid(start, stop)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_TIME_INTERVAL);
    }
    boolean isTrendValid = isTrendValid(start, stop, trend);
    if (!isTrendValid) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TREND_NOT_VALID);
    }
    if (userContext.isNexageUser()) {
      return dashboardFacade.getBuyerMetrics(start, stop, emptySet(), trend);
    } else {
      return dashboardFacade.getBuyerMetrics(start, stop, userContext.getCompanyPids(), trend);
    }
  }

  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
  @Override
  public DashboardMetric getSellerDashboard(Date start, Date stop, boolean trend) {
    if (!isTimeIntervalValid(start, stop)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_TIME_INTERVAL);
    }
    boolean isTrendValid = isTrendValid(start, stop, trend);
    if (!isTrendValid) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TREND_NOT_VALID);
    }
    // For sellers, today is not supported.
    boolean isTimeIntervalToday = isIntervalToday(start, stop);
    if (isTimeIntervalToday) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    if (userContext.isNexageUser()) {
      return dashboardFacade.getSellerMetrics(start, stop, emptySet(), trend);
    } else {
      return dashboardFacade.getSellerMetrics(start, stop, userContext.getCompanyPids(), trend);
    }
  }

  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  @Override
  public DashboardMetric getNexageDashboard(Date start, Date stop, boolean trend) {
    if (!isTimeIntervalValid(start, stop)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_TIME_INTERVAL);
    }
    boolean isTrendValid = isTrendValid(start, stop, trend);
    if (!isTrendValid) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TREND_NOT_VALID);
    }
    return dashboardFacade.getNexageMetrics(start, stop, trend);
  }

  private Map<Long, List<PubSelfServeMetrics>> getSummaryMetricsImpl(
      long pubId, List<SitePubSelfServeView> sites, Date start, Date stop) {
    Set<Long> ids = sites.stream().map(SitePubSelfServeView::getPid).collect(Collectors.toSet());

    return dw.getDashboardSummaryMetrics(pubId, ids, start, stop);
  }

  public Map<Long, List<PubSelfServeMetrics>> getSummaryMetricsDefault(
      long pubId, Date start, Date stop) {
    return getSummaryMetricsImpl(pubId, Collections.<SitePubSelfServeView>emptyList(), start, stop);
  }

  @Cacheable(value = "dashboardSummary", cacheManager = "ehCacheCacheManager")
  public Map<Long, List<PubSelfServeMetrics>> getSummaryMetricsOptimized(
      long pubId, List<SitePubSelfServeView> sites, Date start, Date stop) {
    return getSummaryMetricsImpl(pubId, sites, start, stop);
  }

  private static boolean isTrendValid(Date start, Date stop, boolean trend) {
    boolean result = true;
    if (trend) {
      // Check if the interval between start and stop to determine if trend is allowed
      result = numberOfDaysforInterval(stop, start) <= 7;
    }
    return result;
  }

  private static boolean isTimeIntervalValid(Date start, Date stop) {
    return numberOfDaysforInterval(stop, start) <= 180;
  }

  private static boolean isIntervalToday(Date start, Date stop) {
    boolean isIntervalToday = false;
    Date today = new Date();
    // Check if start date is today
    if (numberOfDaysforInterval(start, today) == 0) {
      // If start date is today, check if the interval requested was just today by comparing it to
      // stop
      if ((numberOfDaysforInterval(start, stop) == 1)) {
        isIntervalToday = true;
      }
    }
    return isIntervalToday;
  }

  private static int numberOfDaysforInterval(Date start, Date stop) {
    return Days.daysBetween(new DateTime(stop), new DateTime(start)).getDays();
  }
}
