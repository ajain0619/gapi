package com.nexage.dw.geneva.dashboard;

import static com.nexage.admin.dw.util.DateUtil.getLastWeekDateRelativeToGivenDate;

import com.google.common.base.Stopwatch;
import com.nexage.admin.dw.util.DateUtil;
import com.nexage.dw.geneva.dashboard.model.BaseNexageMetrics;
import com.nexage.dw.geneva.dashboard.model.BuyerDashboardDetail;
import com.nexage.dw.geneva.dashboard.model.BuyerDashboardSummary;
import com.nexage.dw.geneva.dashboard.model.DashboardMetric;
import com.nexage.dw.geneva.dashboard.model.DashboardMetric.BuyerDashboardMetrics;
import com.nexage.dw.geneva.dashboard.model.DashboardMetric.SellerDashboardMetrics;
import com.nexage.dw.geneva.dashboard.model.NexageDashboardDetail;
import com.nexage.dw.geneva.dashboard.model.SellerDashboardDetail;
import com.nexage.dw.geneva.dashboard.model.SellerDashboardSummary;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component("genevaDashboardFacade")
public class DashboardFacadeImpl implements DashboardFacade {

  private final DashboardDetailDao dashboardDetailDao;

  private final DashboardSummaryDao dashboardSummaryDao;

  @Override
  public DashboardMetric getSellerMetrics(
      Date start, Date stop, Set<Long> companyPids, boolean trend) {
    SellerDashboardMetrics sellerDashboardMetrics = null;
    Stopwatch stopwatch = Stopwatch.createStarted();
    List<SellerDashboardDetail> currentDetailMetrics = null;
    try {
      // Get data for current interval
      if (CollectionUtils.isEmpty(companyPids)) {
        currentDetailMetrics =
            dashboardDetailDao.getSellerMetrics(DateUtil.format(start), DateUtil.format(stop));
      } else {
        currentDetailMetrics =
            dashboardDetailDao.getSellerMetrics(
                DateUtil.format(start), DateUtil.format(stop), companyPids);
      }

      // Get summary from detail metrics
      SellerDashboardSummary currentSummaryMetrics = calculateSellerSummary(currentDetailMetrics);
      // Build the Object that will be serialized to Json based on database object
      sellerDashboardMetrics =
          new SellerDashboardMetrics(currentSummaryMetrics, currentDetailMetrics);
      if (trend) {
        // Get data for previous interval
        // We only need to go back a week because trend is supported only when the data requested is
        // for less than a week.
        SellerDashboardSummary previous = null;
        if (CollectionUtils.isEmpty(companyPids)) {
          previous =
              dashboardSummaryDao.getSellerMetrics(
                  getLastWeekDateRelativeToGivenDate(start), DateUtil.format(stop));
        } else {
          previous =
              dashboardSummaryDao.getSellerMetrics(
                  getLastWeekDateRelativeToGivenDate(start), DateUtil.format(stop), companyPids);
        }
        // Build the trends by comparing current data to previous data
        sellerDashboardMetrics =
            setSellerTrends(sellerDashboardMetrics, currentSummaryMetrics, previous);
      }
    } catch (EmptyResultDataAccessException e) {
      log.warn("No seller dashboard data", e);
      sellerDashboardMetrics = getEmptySellerMetrics();
    }
    stopwatch.stop();
    long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    if (log.isDebugEnabled()) {
      log.debug("Finished Executing Query for Seller Dashboard. Took(ms) :" + millis);
    }
    return new DashboardMetric(sellerDashboardMetrics, null);
  }

  private SellerDashboardSummary calculateSellerSummary(List<SellerDashboardDetail> detailMetrics) {
    long requestSum = 0;
    long servedSum = 0;
    long clicksSum = 0;
    long displayedSum = 0;
    BigDecimal revenueSum = BigDecimal.ZERO;
    for (SellerDashboardDetail metric : detailMetrics) {
      requestSum += metric.getRequests();
      servedSum += metric.getServed();
      clicksSum += metric.getClicks();
      displayedSum += metric.getDisplayed();
      revenueSum = revenueSum.add(metric.getRevenue());
    }
    return new SellerDashboardSummary(requestSum, servedSum, clicksSum, displayedSum, revenueSum);
  }

  @Override
  public DashboardMetric getBuyerMetrics(
      Date start, Date stop, Set<Long> companyPids, boolean trend) {
    BuyerDashboardMetrics buyerDashboardMetrics = null;
    List<BuyerDashboardDetail> currentDetailMetrics = null;
    Stopwatch stopwatch = Stopwatch.createStarted();
    try {
      // Get data for current interval
      if (CollectionUtils.isEmpty(companyPids)) {
        currentDetailMetrics =
            dashboardDetailDao.getBuyerMetrics(DateUtil.format(start), DateUtil.format(stop));
      } else {
        currentDetailMetrics =
            dashboardDetailDao.getBuyerMetrics(
                DateUtil.format(start), DateUtil.format(stop), companyPids);
      }

      // Get summary from detail metrics
      BuyerDashboardSummary currentSummaryMetrics = calculateBuyerSummary(currentDetailMetrics);
      // Build the Object that will be serialized to Json based on database object
      buyerDashboardMetrics =
          new BuyerDashboardMetrics(currentSummaryMetrics, currentDetailMetrics);

      if (trend) {
        // Get data for previous interval
        // We only need to go back a week because trend is supported only when the data requested is
        // for less than a week.
        BuyerDashboardSummary previous = null;
        if (CollectionUtils.isEmpty(companyPids)) {
          previous =
              dashboardSummaryDao.getBuyerMetrics(
                  getLastWeekDateRelativeToGivenDate(start), DateUtil.format(stop));
        } else {
          previous =
              dashboardSummaryDao.getBuyerMetrics(
                  getLastWeekDateRelativeToGivenDate(start), DateUtil.format(stop), companyPids);
        }
        // Build the trends by comparing current data to previous data
        buyerDashboardMetrics =
            setBuyerTrends(buyerDashboardMetrics, currentSummaryMetrics, previous);
      }
    } catch (EmptyResultDataAccessException e) {
      log.warn("No buyer dashboard data", e);
      buyerDashboardMetrics = getEmptyBuyerMetrics();
    }
    stopwatch.stop();
    long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    if (log.isDebugEnabled()) {
      log.debug("Finished Executing Query for Buyer Dashboard. Took(ms) :" + millis);
    }
    return new DashboardMetric(null, buyerDashboardMetrics);
  }

  private BuyerDashboardSummary calculateBuyerSummary(List<BuyerDashboardDetail> detailMetrics) {
    long requestSum = 0;
    long bidsReceivedSum = 0;
    long bidsWonSum = 0;
    long deliveredSum = 0;
    BigDecimal revenueSum = BigDecimal.ZERO;
    for (BuyerDashboardDetail metric : detailMetrics) {
      requestSum += metric.getRequests();
      bidsReceivedSum += metric.getBidsReceived();
      bidsWonSum += metric.getBidsWon();
      deliveredSum += metric.getDelivered();
      revenueSum = revenueSum.add(metric.getRevenue());
    }
    return new BuyerDashboardSummary(
        requestSum, bidsReceivedSum, bidsWonSum, deliveredSum, revenueSum);
  }

  @Override
  public DashboardMetric getNexageMetrics(Date start, Date stop, boolean trend) {
    BuyerDashboardMetrics buyerDashboardMetrics = null;
    SellerDashboardMetrics sellerDashboardMetrics = null;
    Stopwatch stopwatch = Stopwatch.createStarted();
    try {
      // Get data for current interval
      NexageDashboardDetail currentDetailMetrics =
          dashboardDetailDao.getNexageMetrics(DateUtil.format(start), DateUtil.format(stop));
      // Comment out buyers for performance reasons
      // BuyerDashboardSummary currentBuyerSummaryMetrics =
      // calculateBuyerSummary(currentDetailMetrics.getBuyerMetrics());
      SellerDashboardSummary currentSellerSummaryMetrics =
          calculateSellerSummary(currentDetailMetrics.getSellerMetrics());

      // Build the Object that will be serialized to Json based on database object
      // Comment out buyers for performance reasons
      // buyerDashboardMetrics = new BuyerDashboardMetrics(currentBuyerSummaryMetrics,
      // currentDetailMetrics.getBuyerMetrics());
      sellerDashboardMetrics =
          new SellerDashboardMetrics(
              currentSellerSummaryMetrics, currentDetailMetrics.getSellerMetrics());

      if (trend) {
        // Get data for previous interval
        // We only need to go back a week because trend is supported only when the data requested is
        // for less than a week.
        BaseNexageMetrics previous =
            dashboardSummaryDao.getNexageMetrics(
                getLastWeekDateRelativeToGivenDate(start), DateUtil.format(stop));
        // Comment out buyers for performance reasons
        // BuyerDashboardSummary previousBuyer = previous.getBuyerMetrics();
        SellerDashboardSummary previousSeller = previous.getSellerMetrics();

        // Build the Buyer trends by comparing current data to previous data
        // Comment out buyers for performance reasons
        // buyerDashboardMetrics = setBuyerTrends(buyerDashboardMetrics, currentBuyerSummaryMetrics,
        // previousBuyer);

        // Build the Seller trends by comparing current data to previous data
        sellerDashboardMetrics =
            setSellerTrends(sellerDashboardMetrics, currentSellerSummaryMetrics, previousSeller);
      }

    } catch (EmptyResultDataAccessException e) {
      log.warn("No Nexage dashboard data", e);
      buyerDashboardMetrics = getEmptyBuyerMetrics();
      sellerDashboardMetrics = getEmptySellerMetrics();
    }
    stopwatch.stop();
    long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    if (log.isDebugEnabled()) {
      log.debug("Finished Executing Query for Nexage Dashboard. Took(ms) :" + millis);
    }
    return new DashboardMetric(sellerDashboardMetrics, buyerDashboardMetrics);
  }

  private BuyerDashboardMetrics setBuyerTrends(
      BuyerDashboardMetrics buyerDashboardMetrics,
      BuyerDashboardSummary current,
      BuyerDashboardSummary previous) {

    // Build the Buyer trends by comparing current data to previous data
    buyerDashboardMetrics
        .getRequests()
        .setTrend(Long.compare(current.getRequests(), previous.getRequests()));
    buyerDashboardMetrics
        .getBidRate()
        .setTrend(Double.compare(current.getBidRate(), previous.getBidRate()));
    buyerDashboardMetrics
        .getImpressions()
        .setTrend(Long.compare(current.getDelivered(), previous.getDelivered()));
    buyerDashboardMetrics
        .getWinRate()
        .setTrend(Double.compare(current.getWinRate(), previous.getWinRate()));
    if (current.getRevenue() != null && previous.getRevenue() != null) {
      buyerDashboardMetrics
          .getSpend()
          .setTrend(current.getRevenue().compareTo(previous.getRevenue()));
    }
    if (current.geteCpm() != null && previous.geteCpm() != null) {
      buyerDashboardMetrics.geteCpm().setTrend(current.geteCpm().compareTo(previous.geteCpm()));
    }
    return buyerDashboardMetrics;
  }

  private SellerDashboardMetrics setSellerTrends(
      SellerDashboardMetrics sellerDashboardMetrics,
      SellerDashboardSummary current,
      SellerDashboardSummary previous) {

    // Build the Seller trends by comparing current data to previous data
    sellerDashboardMetrics
        .getRequests()
        .setTrend(Long.compare(current.getRequests(), previous.getRequests()));
    sellerDashboardMetrics
        .getClicks()
        .setTrend(Long.compare(current.getClicks(), previous.getClicks()));
    sellerDashboardMetrics
        .getFillRate()
        .setTrend(Double.compare(current.getFillRate(), previous.getFillRate()));
    sellerDashboardMetrics.getCtr().setTrend(Double.compare(current.getCtr(), previous.getCtr()));
    sellerDashboardMetrics
        .getRevenue()
        .setTrend(current.getRevenue().compareTo(previous.getRevenue()));
    sellerDashboardMetrics.geteCpm().setTrend(current.geteCpm().compareTo(previous.geteCpm()));
    sellerDashboardMetrics.getRpm().setTrend(current.getRpm().compareTo(previous.getRpm()));

    return sellerDashboardMetrics;
  }

  private SellerDashboardMetrics getEmptySellerMetrics() {
    return SellerDashboardMetrics.emptyMetric();
  }

  private BuyerDashboardMetrics getEmptyBuyerMetrics() {
    return BuyerDashboardMetrics.emptyMetric();
  }
}
