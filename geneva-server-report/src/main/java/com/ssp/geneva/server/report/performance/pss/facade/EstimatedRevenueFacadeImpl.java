package com.ssp.geneva.server.report.performance.pss.facade;

import com.google.common.base.Stopwatch;
import com.ssp.geneva.server.report.performance.pss.dao.EstimatedRevenuePubSelfServiceDao;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdNetworksForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdvertiserForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueForPubSelfServe;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component("estimatedRevenueFacade")
public class EstimatedRevenueFacadeImpl implements EstimatedRevenueFacade {

  private final EstimatedRevenuePubSelfServiceDao estimatedRevenue;

  @Override
  public EstimatedRevenueForPubSelfServe getEstimatedRevenueForPubSelfServe(
      long publisher, LocalDate start, LocalDate stop, String loggedInUser) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    EstimatedRevenueForPubSelfServe estimatedRevenueAggregated =
        estimatedRevenue.getEstimatedRevenueForPss(publisher, start, stop, loggedInUser);
    stopwatch.stop();
    long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    log.info("Finished Executing Query for Estimated Revenue in (ms):" + millis);
    return estimatedRevenueAggregated;
  }

  @Override
  public EstimatedRevenueByAdNetworksForPubSelfServ getEstimatedRevenueByAdNetworksForPubSelfServ(
      long publisher, LocalDate start, LocalDate stop, String loggedInUser) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    EstimatedRevenueByAdNetworksForPubSelfServ estimatedRevenueByAdNetworksForPubSelfServ =
        estimatedRevenue.getEstimatedRevenueByAdNetworksForPss(
            publisher, start, stop, loggedInUser);
    stopwatch.stop();
    long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    log.info(
        "Finished Executing Query for Estimated Revenue drilldown by Ad Networks in (ms):"
            + millis);
    return estimatedRevenueByAdNetworksForPubSelfServ;
  }

  @Override
  public EstimatedRevenueByAdvertiserForPubSelfServ getEstimatedRevenueByAdvertiserForPubSelfServ(
      long publisher, LocalDate start, LocalDate stop, String loggedInUser) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    EstimatedRevenueByAdvertiserForPubSelfServ estimatedRevenueByAdvertiserForPubSelfServ =
        estimatedRevenue.getEstimatedRevenueByAdvertiserForPss(
            publisher, start, stop, loggedInUser);
    stopwatch.stop();
    long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    log.info(
        "Finished Executing Query for Estimated Revenue drilldown by Advertiser in (ms):" + millis);
    return estimatedRevenueByAdvertiserForPubSelfServ;
  }
}
