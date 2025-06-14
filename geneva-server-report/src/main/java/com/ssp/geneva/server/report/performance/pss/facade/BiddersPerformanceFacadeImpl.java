package com.ssp.geneva.server.report.performance.pss.facade;

import com.google.common.base.Stopwatch;
import com.nexage.admin.dw.util.DateUtil;
import com.ssp.geneva.server.report.performance.pss.dao.BiddersPerformancePubSelfServeDao;
import com.ssp.geneva.server.report.performance.pss.model.BiddersPerformanceForPubSelfServe;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component("biddersPerformanceFacade")
public class BiddersPerformanceFacadeImpl implements BiddersPerformanceFacade {

  private final BiddersPerformancePubSelfServeDao biddersPerformance;

  @Override
  public List<BiddersPerformanceForPubSelfServe> getBiddersPerformanceForPubSelfServe(
      Set<Long> sitePids, Date start, Date stop, String loggedInUser) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    List<BiddersPerformanceForPubSelfServe> biddersPerformances =
        biddersPerformance.getBiddersPerformancePss(
            DateUtil.format(start), DateUtil.format(stop), sitePids, loggedInUser);
    stopwatch.stop();
    long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    log.info("Finished Executing Query for Bidders performance in (ms):" + millis);
    return biddersPerformances;
  }
}
