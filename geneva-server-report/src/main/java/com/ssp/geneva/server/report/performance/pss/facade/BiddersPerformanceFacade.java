package com.ssp.geneva.server.report.performance.pss.facade;

import com.ssp.geneva.server.report.performance.pss.model.BiddersPerformanceForPubSelfServe;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface BiddersPerformanceFacade {

  List<BiddersPerformanceForPubSelfServe> getBiddersPerformanceForPubSelfServe(
      Set<Long> sitePids, Date start, Date stop, String loggedUser);
}
